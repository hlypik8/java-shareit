package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingDtoMapperTest {

    @Test
    void toBookingDto_shouldMapAllFields() {
        User booker = new User(1, "Alice", "alice@mail.com");
        Item item = new Item(2, "Дрель", "Электрическая", true, 10, new Request());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        Booking booking = new Booking();
        booking.setId(100);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        BookingDto dto = BookingDtoMapper.toBookingDto(booking);

        assertNotNull(dto);
        assertEquals(100, dto.getId());
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());

        ItemDto itemDto = dto.getItem();
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());

        UserDto userDto = dto.getBooker();
        assertNotNull(userDto);
        assertEquals(booker.getId(), userDto.getId());
        assertEquals(booker.getName(), userDto.getName());
    }

    @Test
    void toBooking_shouldMapFromCreateDto() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = start.plusDays(5);
        BookingCreateDto createDto = new BookingCreateDto(5, start, end);

        Item item = new Item(5, "Молоток", "Тяжелый", true, 20, null);
        User user = new User(2, "Bob", "bob@mail.com");

        Booking booking = BookingDtoMapper.toBooking(createDto, item, user);

        assertNotNull(booking);
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
        assertEquals(booking.getStatus(), BookingStatus.WAITING, "Status должен быть WAITING");
        assertNull(booking.getId(), "Id должен быть null, т.к. в маппере не устанавливается");
    }
}