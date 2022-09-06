package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
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
    ItemRequest request;
}
