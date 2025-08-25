package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.error.exceptions.InvalidItemOwnerException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.UnavailableItemException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

    public BookingDto addBooking(BookingCreateDto bookingCreateDto, Integer userId)
            throws NotFoundException, UnavailableItemException {

        log.info("Создание нового бронирования для пользователя {}", userId);

        User user = UserDtoMapper.toUser(userService.getUserById(userId));
        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Вещь с id " + bookingCreateDto.getItemId() + " не найдена"));

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
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new InvalidItemOwnerException("Вы не являетесь владельцем вещи");
        }
        bookingRepository.save(booking);
        log.info("Верификация прошла успешно. Cтатус аренды с ID: " + bookingId + " " + booking.getStatus().toString());
        return BookingDtoMapper.toBookingDto(booking);
    }

    public BookingDto getBookingById(Integer bookingId, Integer userId)
            throws NotFoundException, InvalidItemOwnerException {
        log.info("Запрос аренды с ID: {}", bookingId);

        if (!userService.isUserExists(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Аренда с ID: " + bookingId + " не найдена"));

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

        User user = UserDtoMapper.toUser(userService.getUserById(userId));

        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByBooker(user, sortByStartDesc);
            case CURRENT -> bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, now, now, sortByStartDesc);
            case PAST -> bookingRepository.findAllByBookerAndEndBefore(user, now, sortByStartDesc);
            case FUTURE -> bookingRepository.findAllByBookerAndStartAfter(user, now, sortByStartDesc);
            case WAITING -> bookingRepository.findAllByStatusAndBooker(BookingStatus.WAITING, user, sortByStartDesc);
            case REJECTED -> bookingRepository.findAllByStatusAndBooker(BookingStatus.REJECTED, user, sortByStartDesc);
        };

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

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByItemOwner(userId, sortByStartDesc);
            case CURRENT ->
                    bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(userId, now, now, sortByStartDesc);
            case PAST -> bookingRepository.findAllByItemOwnerAndEndBefore(userId, now, sortByStartDesc);
            case FUTURE -> bookingRepository.findAllByItemOwnerAndStartAfter(userId, now, sortByStartDesc);
            case WAITING ->
                    bookingRepository.findAllByStatusAndItemOwner(BookingStatus.WAITING, userId, sortByStartDesc);
            case REJECTED ->
                    bookingRepository.findAllByStatusAndItemOwner(BookingStatus.REJECTED, userId, sortByStartDesc);
        };

        List<BookingDto> bookingDto = bookings.stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());

        log.info("Запрос бронирований выполнен size: {}", bookingDto.size());

        return bookingDto;
    }
}
