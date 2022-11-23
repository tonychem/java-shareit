package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long userId, BookingDto bookingDto);

    BookingDto setBookingStatus(long userId, long bookingId, boolean approved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getListOfBookingsByState(long userId, String state, Integer from, Integer size);

    List<BookingDto> getListOfBookedItemsByOwner(long userId, String state, Integer from, Integer size);
}
