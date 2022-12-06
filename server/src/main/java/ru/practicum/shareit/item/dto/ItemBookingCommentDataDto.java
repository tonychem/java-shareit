package ru.practicum.shareit.item.dto;

import lombok.Value;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Value
public class ItemBookingCommentDataDto {
    long id;
    @Nullable
    String name;
    @Nullable
    String description;
    @Nullable
    Boolean available;
    @Nullable
    User owner;
    @Nullable
    BookingDtoShort lastBooking;
    @Nullable
    BookingDtoShort nextBooking;
    @Nullable
    List<OutcomingCommentDto> comments;
}
