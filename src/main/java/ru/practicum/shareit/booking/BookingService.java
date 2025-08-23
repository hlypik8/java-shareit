package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingDtoMapper bookingDtoMapper;
    private final ItemDtoMapper itemDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    public BookingDto addBooking(BookingCreateDto bookingCreateDto, Integer userId) throws NotFoundException {

        log.info("Создание нового бронирования для пользователя {}", userId);

        User user = userDtoMapper.toUser(userService.getUserById(userId));
        Item item = itemDtoMapper.toItem(itemService.getItemById(bookingCreateDto.getItemId()));

        Booking booking = bookingRepository.save(bookingDtoMapper.toBooking(bookingCreateDto, item, user));

        log.debug("Бронирование успешно создано ID: {}", booking.getId());

        return bookingDtoMapper.toBookingDto(booking);
    }


}
