package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid BookingRequestDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<Object> setBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam("approved") boolean approved,
                                                   @PathVariable long bookingId) {
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getListOfBookingsByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                           @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                                           @Positive @RequestParam(name = "size", required = false) Integer size) {
        return bookingClient.listOfBookingsByState(userId, state.toUpperCase(), from, size);
    }

    @GetMapping(value = "/owner")
    public ResponseEntity<Object> getListOfBookedItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                              @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                                              @Positive @RequestParam(name = "size", required = false) Integer size) {
        return bookingClient.listOfBookedItemsByOwner(userId, state.toUpperCase(), from, size);
    }
}
