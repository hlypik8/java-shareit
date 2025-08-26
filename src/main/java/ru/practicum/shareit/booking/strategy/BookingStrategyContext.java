package ru.practicum.shareit.booking.strategy;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.strategy.bookingStrateges.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BookingStrategyContext {
    private final Map<BookingState, BookingStrategy> strategies;

    public BookingStrategyContext(List<BookingStrategy> strategyList) {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> {
                            if (strategy instanceof AllBookingsStrategy) return BookingState.ALL;
                            if (strategy instanceof CurrentBookingsStrategy) return BookingState.CURRENT;
                            if (strategy instanceof FutureBookingsStrategy) return BookingState.FUTURE;
                            if (strategy instanceof PastBookingsStrategy) return BookingState.PAST;
                            if (strategy instanceof RejectedBookingsStrategy) return BookingState.REJECTED;
                            if (strategy instanceof WaitingBookingsStrategy) return BookingState.WAITING;
                            throw new IllegalArgumentException("Неизвестный тип стратегии");
                        },
                        Function.identity()
                ));
    }

    public List<Booking> executeStrategy(BookingState state, BookingRepository repository,
                                         User user, Sort sort, LocalDateTime now) {
        BookingStrategy strategy = strategies.get(state);
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid booking state: " + state);
        }
        return strategy.getBookings(repository, user, sort, now);
    }

    public List<Booking> executeStrategyForOwner(BookingState state, BookingRepository repository,
                                                 Integer userId, Sort sort, LocalDateTime now) {
        BookingStrategy strategy = strategies.get(state);
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid booking state: " + state);
        }
        return strategy.getBookingsForOwner(repository, userId, sort, now);
    }
}