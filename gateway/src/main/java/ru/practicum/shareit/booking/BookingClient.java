package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        return null;
    }

    public ResponseEntity<Object> getBookingDtoById(Long userId, Integer bookingId) {
        Map <String, Object> params = Map.of(
                "bookingId", bookingId
        );
        return get("/" + bookingId, userId, params);
    }

    public ResponseEntity<Object> addBooking(BookingCreateDto bookingCreateDto, Integer userId) {
        return post("", userId, bookingCreateDto);
    }

    public ResponseEntity<Object> bookingVerification(Integer userId, Integer bookingId, boolean approved) {
        Map <String, Object> params = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved=" + approved, userId, params);
    }

    public ResponseEntity<Object> getOwnerBookings(Integer userId, String state) {
        return get("/owner", userId);
    }
}
