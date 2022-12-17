package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDto setBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam("approved") boolean approved,
                                       @PathVariable long bookingId) {
        return bookingService.setBookingStatus(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getListOfBookingsByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                     @RequestParam(name = "from", required = false) Integer from,
                                                     @RequestParam(name = "size", required = false) Integer size) {
        return bookingService.getListOfBookingsByState(userId, state.toUpperCase(), from, size);
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> getListOfBookedItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                        @RequestParam(name = "from", required = false) Integer from,
                                                        @RequestParam(name = "size", required = false) Integer size) {
        return bookingService.getListOfBookedItemsByOwner(userId, state.toUpperCase(), from, size);
    }
}
