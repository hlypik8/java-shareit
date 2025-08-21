package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingDtoMapper bookingDtoMapper;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    public BookingDto addBooking(BookingCreateDto bookingCreateDto, Integer userId) throws NotFoundException {

        log.info("Создание нового бронирования для пользователя {}", userId);

        ItemDto itemDto = itemService.getItemById(bookingCreateDto.getItemId());
        UserDto userDto = userService.getUserById(userId);

        Booking booking = bookingRepository.save(bookingDtoMapper.toBooking(bookingCreateDto, itemDto, userDto));

        log.debug("Бронирование успешно создано ID: {}", booking.getId());

        return bookingDtoMapper.toBookingDto(booking);
    }


}
