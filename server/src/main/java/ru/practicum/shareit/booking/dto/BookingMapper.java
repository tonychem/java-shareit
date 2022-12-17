package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(),
                booking.getEnd(), booking.getItem(), booking.getBooker(), booking.getStatus(), booking.getItem().getId(),
                booking.getBooker().getId());
    }

    public Booking toBooking(BookingDto bookingDto) {

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null ||
                bookingDto.getItemId() == 0) {
            throw new IllegalStateException("Попытка создать бронирование с null-полями");
        }

        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(),
                bookingDto.getItem(), bookingDto.getBooker(), bookingDto.getStatus());
    }

    public BookingDtoShort toBookingDtoShort(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingDtoShort(booking.getId(), booking.getBooker().getId());
    }
}
