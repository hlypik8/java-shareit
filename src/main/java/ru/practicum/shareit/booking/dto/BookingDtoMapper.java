package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

@Component
@RequiredArgsConstructor
public class BookingDtoMapper {

    private final ItemDtoMapper itemDtoMapper;
    private final UserDtoMapper userDtoMapper;

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemDtoMapper.toDto(booking.getItem()),
                userDtoMapper.toDto(booking.getBooker()),
                booking.getStatus());
    }

    public Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setBooker(user);

        return booking;
    }
}
