package ru.practicum.shareit.booking.strategy;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStrategy {
    List<Booking> getBookings(BookingRepository repository, User user, Sort sort, LocalDateTime now);

    List<Booking> getBookingsForOwner(BookingRepository repository, Integer userId, Sort sort, LocalDateTime now);

}
