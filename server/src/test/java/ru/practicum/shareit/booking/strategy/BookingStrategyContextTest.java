package ru.practicum.shareit.booking.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.strategy.bookingStrateges.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingStrategyContextTest {

    private BookingStrategyContext context;
    @Mock
    private BookingRepository repository;
    private User user;
    private Sort sort;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        context = new BookingStrategyContext(List.of(
                new AllBookingsStrategy(),
                new CurrentBookingsStrategy(),
                new FutureBookingsStrategy(),
                new PastBookingsStrategy(),
                new RejectedBookingsStrategy(),
                new WaitingBookingsStrategy()
        ));
        user = new User(1, "Alice", "alice@mail.com");
        sort = Sort.by("start").descending();
        now = LocalDateTime.now();
    }

    @Test
    void executeStrategy_all_shouldDelegateToRepository() {
        Booking booking = new Booking();
        when(repository.findAllByBooker(user, sort)).thenReturn(List.of(booking));

        List<Booking> result = context.executeStrategy(BookingState.ALL, repository, user, sort, now);

        assertThat(result).containsExactly(booking);
        verify(repository).findAllByBooker(user, sort);
    }

    @Test
    void executeStrategyForOwner_future_shouldDelegateToRepository() {
        Booking booking = new Booking();
        when(repository.findAllByItemOwnerAndStartAfter(1, now, sort)).thenReturn(List.of(booking));

        List<Booking> result = context.executeStrategyForOwner(BookingState.FUTURE, repository, 1, sort, now);

        assertThat(result).containsExactly(booking);
        verify(repository).findAllByItemOwnerAndStartAfter(1, now, sort);
    }

    @Test
    void executeStrategy_invalidState_shouldThrow() {
        assertThatThrownBy(() ->
                context.executeStrategy(null, repository, user, sort, now)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}