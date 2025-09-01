package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.exceptions.InvalidItemOwnerException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.UnavailableItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private BookingCreateDto bookingCreateDto;

    private final String sharerId = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        bookingCreateDto = new BookingCreateDto(10,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        bookingDto = new BookingDto(
                1,
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(),
                new ItemDto(10, "Дрель", "Беспроводная", true, 1, null),
                new UserDto(1, "Alice", "alice@mail.com"),
                BookingStatus.WAITING
        );
    }

    @Test
    void addBooking_success() throws Exception {
        Mockito.when(bookingService.addBooking(any(), anyInt())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(sharerId, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId())))
                .andExpect(jsonPath("$.item.id", is(10)))
                .andExpect(jsonPath("$.booker.id", is(1)));
    }

    @Test
    void addBooking_unavailableItem_shouldReturn400() throws Exception {
        Mockito.when(bookingService.addBooking(any(), anyInt()))
                .thenThrow(new UnavailableItemException("Эта вещь не доступна для аренды"));

        mockMvc.perform(post("/bookings")
                        .header(sharerId, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookingVerification_success() throws Exception {
        Mockito.when(bookingService.bookingVerification(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(sharerId, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void bookingVerification_notOwner_shouldReturn403() throws Exception {
        Mockito.when(bookingService.bookingVerification(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new InvalidItemOwnerException("Вы не являетесь владельцем вещи"));

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(sharerId, 2))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBooking_success() throws Exception {
        Mockito.when(bookingService.getBookingDtoById(anyInt(), anyInt())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(sharerId, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getBooking_notFound_shouldReturn404() throws Exception {
        Mockito.when(bookingService.getBookingDtoById(anyInt(), anyInt()))
                .thenThrow(new NotFoundException("Аренда не найдена"));

        mockMvc.perform(get("/bookings/99")
                        .header(sharerId, 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingList_success() throws Exception {
        Mockito.when(bookingService.getBookingList(anyString(), anyInt())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(sharerId, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getOwnerBookings_success() throws Exception {
        Mockito.when(bookingService.getOwnerBookings(anyInt(), anyString())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(sharerId, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}