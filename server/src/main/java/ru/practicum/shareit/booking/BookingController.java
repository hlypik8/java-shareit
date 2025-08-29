package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.error.exceptions.InvalidItemOwnerException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.UnavailableItemException;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final String sharerIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingCreateDto bookingCreateDto,
                                 @RequestHeader(sharerIdHeader) Integer userId)
            throws NotFoundException, UnavailableItemException {
        return bookingService.addBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingVerification(@RequestHeader(sharerIdHeader) Integer userId,
                                          @PathVariable Integer bookingId,
                                          @RequestParam("approved") boolean approved)
            throws NotFoundException, InvalidItemOwnerException {
        return bookingService.bookingVerification(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Integer bookingId,
                                 @RequestHeader(sharerIdHeader) Integer userId)
            throws NotFoundException, InvalidItemOwnerException {
        return bookingService.getBookingDtoById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingList(@RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                           @RequestHeader(sharerIdHeader) Integer userId)
            throws NotFoundException {
        return bookingService.getBookingList(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(sharerIdHeader) Integer userId,
                                             @RequestParam(name = "state", required = false, defaultValue = "ALL") String state)
            throws NotFoundException {
        return bookingService.getOwnerBookings(userId, state);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(InvalidItemOwnerException e) {
        return new ErrorResponse("Ошибка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(NotFoundException e) {
        return new ErrorResponse("Ошибка", e.getMessage());
    }
}
