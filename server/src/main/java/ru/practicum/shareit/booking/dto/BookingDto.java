package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    long id;
    @Nullable
    LocalDateTime start;
    @Nullable
    LocalDateTime end;
    @Nullable
    Item item;
    @Nullable
    User booker;
    @Nullable
    BookingStatus status;
    long itemId;
    long bookerId;
}
