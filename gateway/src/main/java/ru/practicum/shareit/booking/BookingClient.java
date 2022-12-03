package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String url) {
        super(url + API_PREFIX);
    }

    public ResponseEntity<Object> createBooking(long userId, BookingRequestDto bookingRequestDto) {
        return post("/", userId, bookingRequestDto);
    }

    public ResponseEntity<Object> updateBookingStatus(long userId, long bookingId, boolean approved) {
        Map<String, Object> params = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, params, null);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> listOfBookingsByState(long userId, String state, Integer from, Integer size) {
        if (from == null || size == null) {
            return get("?state=" + state, userId);
        }

        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> listOfBookedItemsByOwner(long userId, String state, Integer from, Integer size) {
        if (from == null || size == null) {
            return get("/owner?state=" + state, userId);
        }

        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}
