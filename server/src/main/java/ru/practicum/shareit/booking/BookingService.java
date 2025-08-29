package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.strategy.BookingStrategyContext;
import ru.practicum.shareit.error.exceptions.InvalidItemOwnerException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.UnavailableItemException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository; //Здесь можно было бы использовать сервис, но тогда получается циклическая зависимость сервисов Booking и Item
    private final BookingStrategyContext bookingStrategyContext;

    Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

    public BookingDto addBooking(BookingCreateDto bookingCreateDto, Integer userId)
            throws NotFoundException, UnavailableItemException {

        log.info("Создание нового бронирования для пользователя {}", userId);

        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingCreateDto.getItemId());

        if (!item.getAvailable()) {
            throw new UnavailableItemException("Эта вещь не доступна для аренды");
        }

        Booking booking = bookingRepository.save(BookingDtoMapper.toBooking(bookingCreateDto, item, user));

        log.debug("Бронирование успешно создано ID: {}", booking.getId());

        return BookingDtoMapper.toBookingDto(booking);
    }

    public BookingDto bookingVerification(Integer userId, Integer bookingId, boolean approved)
            throws NotFoundException, InvalidItemOwnerException {
        log.info("Верификация аренды с ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Аренда с Id: " + bookingId + " не найдена"));

        if (booking.getItem().getOwner().equals(userId)) {
            var status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
            booking.setStatus(status);
        } else {
            throw new InvalidItemOwnerException("Вы не являетесь владельцем вещи");
        }
        bookingRepository.save(booking);
        log.info("Верификация прошла успешно. Cтатус аренды с ID: " + bookingId + " " + booking.getStatus().toString());
        return BookingDtoMapper.toBookingDto(booking);
    }

    public BookingDto getBookingDtoById(Integer userId, Integer bookingId)
            throws NotFoundException, InvalidItemOwnerException {
        log.info("Запрос аренды с ID: {}", bookingId);

        if (!userService.isUserExists(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        Booking booking = getBookingById(bookingId);

        boolean isOwner = booking.getItem().getOwner().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);

        if (!isOwner && !isBooker) {
            throw new InvalidItemOwnerException("Вы не являетесь владельцем или арендатором вещи");
        } else {
            log.info("Получена аренда с ID: {}", booking.getId());
            return BookingDtoMapper.toBookingDto(booking);
        }
    }

    public List<BookingDto> getBookingList(String state, Integer userId) throws NotFoundException {
        log.info("Запрос бронирований пользователя с ID {}, state = {}", userId, state);

        User user = userService.getUserById(userId);

        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingStrategyContext.executeStrategy(
                bookingState, bookingRepository, user, sortByStartDesc, now
        );

        List<BookingDto> bookingDto = bookings.stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());

        log.info("Запрос бронирований выполнен size: {}", bookingDto.size());

        return bookingDto;
    }

    public List<BookingDto> getOwnerBookings(Integer userId, String state) throws NotFoundException {
        log.info("Запрос бронирований пользователя с ID {}, state = {}", userId, state);

        if (!userService.isUserExists(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingStrategyContext.executeStrategyForOwner(
                bookingState, bookingRepository, userId, sortByStartDesc, now
        );

        List<BookingDto> bookingDto = bookings.stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());

        log.info("Запрос бронирований выполнен size: {}", bookingDto.size());

        return bookingDto;
    }

    public Booking getBookingById(Integer bookingId) throws NotFoundException {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Аренда с ID: " + bookingId + " не найдена"));
    }
}
