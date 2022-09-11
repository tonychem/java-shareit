package ru.practicum.shareit.item.dto;

import lombok.Value;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Value
public class ItemBookingCommentDataDto {
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
    @Nullable
    List<OutcomingCommentDto> comments;
}
