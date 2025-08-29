package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    private final String sharerIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody @Valid BookingCreateDto bookingCreateDto,
                                             @RequestHeader(sharerIdHeader) Integer userId) {
        return bookingClient.addBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingVerification(@RequestHeader(sharerIdHeader) Integer userId,
                                          @PathVariable Integer bookingId,
                                          @RequestParam("approved") boolean approved) {
        return bookingClient.bookingVerification(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(sharerIdHeader) Integer userId) {
        return bookingClient.getBookingDtoById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingList(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(sharerIdHeader) Integer userId,
                                             @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingClient.getOwnerBookings(userId, state);
    }
}
