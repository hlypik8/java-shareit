package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.error.exceptions.InvalidItemOwnerException;
import ru.practicum.shareit.error.exceptions.NotFoundException;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final String sharerIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                 @RequestHeader(sharerIdHeader) Integer userId) throws NotFoundException {
        return bookingService.addBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingVerification(@RequestHeader(sharerIdHeader) Integer userId,
                                    @PathVariable Integer bookingId,
                                    @RequestParam("approved") boolean approved)
            throws NotFoundException, InvalidItemOwnerException {
     return bookingService.bookingVerification(userId, bookingId, approved);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(InvalidItemOwnerException e){
        return new ErrorResponse("Ошибка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(NotFoundException e){
        return new ErrorResponse("Ошибка", e.getMessage());
    }
}
