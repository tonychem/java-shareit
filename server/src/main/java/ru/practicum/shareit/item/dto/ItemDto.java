package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.user.model.User;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
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
    Long requestId;
}
