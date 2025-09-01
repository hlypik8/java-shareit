package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.strategy.BookingStrategyContext;
import ru.practicum.shareit.error.exceptions.InvalidItemOwnerException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.UnavailableItemException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Mock
    private BookingStrategyContext bookingStrategyContext;
    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1, "Alice", "alice@mail.com");
        item = new Item(10, "Drill", "Cordless drill", true, user.getId(), null);
    }

    @Test
    void addBooking_success() throws Exception {
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemService.getItemById(item.getId())).thenReturn(item);

        Booking savedBooking = new Booking();
        savedBooking.setId(100);
        savedBooking.setItem(item);
        savedBooking.setBooker(user);
        savedBooking.setStart(dto.getStart());
        savedBooking.setEnd(dto.getEnd());
        savedBooking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.save(any())).thenReturn(savedBooking);

        BookingDto result = bookingService.addBooking(dto, user.getId());

        assertNotNull(result);
        assertEquals(100, result.getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(user.getId(), result.getBooker().getId());

        verify(bookingRepository).save(any());
    }

    @Test
    void addBooking_itemUnavailable() throws Exception {
        item.setAvailable(false);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemService.getItemById(item.getId())).thenReturn(item);

        assertThrows(UnavailableItemException.class, () -> bookingService.addBooking(dto, user.getId()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void bookingVerification_ownerApproves() throws Exception {
        Booking booking = new Booking();
        booking.setId(200);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(200)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.bookingVerification(item.getOwner(), 200, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void bookingVerification_notOwner_shouldThrow() {
        Booking booking = new Booking();
        booking.setId(200);
        booking.setItem(item);
        booking.setBooker(user);

        when(bookingRepository.findById(200)).thenReturn(Optional.of(booking));

        assertThrows(InvalidItemOwnerException.class,
                () -> bookingService.bookingVerification(999, 200, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingDtoById_asOwner() throws Exception {
        Booking booking = new Booking();
        booking.setId(300);
        booking.setItem(item);
        booking.setBooker(user);

        when(userService.isUserExists(item.getOwner())).thenReturn(true);
        when(bookingRepository.findById(300)).thenReturn(Optional.of(booking));

        BookingDto dto = bookingService.getBookingDtoById(item.getOwner(), 300);

        assertEquals(300, dto.getId());
        assertEquals(item.getId(), dto.getItem().getId());
    }

    @Test
    void getBookingDtoById_asOtherUser_shouldThrow() {
        Booking booking = new Booking();
        booking.setId(300);
        booking.setItem(item);
        booking.setBooker(user);

        when(userService.isUserExists(999)).thenReturn(true);
        when(bookingRepository.findById(300)).thenReturn(Optional.of(booking));

        assertThrows(InvalidItemOwnerException.class,
                () -> bookingService.getBookingDtoById(999, 300));
    }

    @Test
    void getBookingList_shouldCallStrategy() throws Exception {
        Booking booking = new Booking();
        booking.setId(400);
        booking.setItem(item);
        booking.setBooker(user);

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingStrategyContext.executeStrategy(eq(BookingState.ALL), eq(bookingRepository), eq(user), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> list = bookingService.getBookingList("ALL", user.getId());

        assertEquals(1, list.size());
        assertEquals(400, list.get(0).getId());
    }

    @Test
    void getOwnerBookings_shouldCallStrategyForOwner() throws Exception {
        Booking booking = new Booking();
        booking.setId(500);
        booking.setItem(item);
        booking.setBooker(user);

        when(userService.isUserExists(user.getId())).thenReturn(true);
        when(bookingStrategyContext.executeStrategyForOwner(eq(BookingState.ALL), eq(bookingRepository), eq(user.getId()), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> list = bookingService.getOwnerBookings(user.getId(), "ALL");

        assertEquals(1, list.size());
        assertEquals(500, list.get(0).getId());
    }

    @Test
    void getBookingById_notFound_shouldThrow() {
        when(bookingRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(999));
    }
}