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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PastBookingsStrategyTest {
    private final PastBookingsStrategy strategy = new PastBookingsStrategy();

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
    void getBookings_shouldReturnFromRepository() {
        Booking booking = new Booking();
        when(repository.findAllByBookerAndEndBefore(user, now, sort)).thenReturn(List.of(booking));

        List<Booking> result = strategy.getBookings(repository, user, sort, now);

        assertThat(result).containsExactly(booking);
        verify(repository).findAllByBookerAndEndBefore(user, now, sort);
    }

    @Test
    void getBookingsForOwner_shouldReturnFromRepository() {
        Booking booking = new Booking();
        when(repository.findAllByItemOwnerAndEndBefore(1, now, sort)).thenReturn(List.of(booking));

        List<Booking> result = strategy.getBookingsForOwner(repository, 1, sort, now);

        assertThat(result).containsExactly(booking);
        verify(repository).findAllByItemOwnerAndEndBefore(1, now, sort);
    }
}