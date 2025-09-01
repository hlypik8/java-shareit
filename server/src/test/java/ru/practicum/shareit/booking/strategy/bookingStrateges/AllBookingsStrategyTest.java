package ru.practicum.shareit.booking.strategy.bookingStrateges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllBookingsStrategyTest {

    private final AllBookingsStrategy strategy = new AllBookingsStrategy();

    @Mock
    private BookingRepository repository;

    private User user;
    private Sort sort;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        user = new User(1, "Alice", "alice@mail.com");
        sort = Sort.by(Sort.Direction.DESC, "start");
        now = LocalDateTime.now();
    }

    @Test
    void getBookings_shouldCallRepositoryAndReturnResult() {
        Booking booking = new Booking();
        when(repository.findAllByBooker(eq(user), eq(sort)))
                .thenReturn(List.of(booking));

        List<Booking> result = strategy.getBookings(repository, user, sort, now);

        assertThat(result).containsExactly(booking);
        verify(repository, times(1)).findAllByBooker(user, sort);
    }

    @Test
    void getBookingsForOwner_shouldCallRepositoryAndReturnResult() {
        Booking booking = new Booking();
        when(repository.findAllByItemOwner(eq(1), eq(sort)))
                .thenReturn(List.of(booking));

        List<Booking> result = strategy.getBookingsForOwner(repository, 1, sort, now);

        assertThat(result).containsExactly(booking);
        verify(repository, times(1)).findAllByItemOwner(1, sort);
    }
}