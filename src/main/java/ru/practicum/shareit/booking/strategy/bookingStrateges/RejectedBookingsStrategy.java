package ru.practicum.shareit.booking.strategy.bookingStrateges;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.strategy.BookingStrategy;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RejectedBookingsStrategy implements BookingStrategy {
    @Override
    public List<Booking> getBookings(BookingRepository repository, User user, Sort sort, LocalDateTime now) {
        return repository.findAllByStatusAndBooker(BookingStatus.REJECTED, user, sort);
    }

    @Override
    public List<Booking> getBookingsForOwner(BookingRepository repository, Integer userId, Sort sort, LocalDateTime now) {
        return repository.findAllByStatusAndItemOwner(BookingStatus.REJECTED, userId, sort);
    }
}