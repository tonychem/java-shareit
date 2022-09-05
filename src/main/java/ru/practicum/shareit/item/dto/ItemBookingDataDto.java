package ru.practicum.shareit.item.dto;

import lombok.Value;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Value
public class ItemBookingDataDto {
    long id;
    @Nullable
    @NotBlank
    String name;
    @Nullable
    @NotBlank
    String description;
    @Nullable
    Boolean available;
    @Nullable
    User owner;
    @Nullable
    BookingDtoShort lastBooking;
    @Nullable
    BookingDtoShort nextBooking;
}
