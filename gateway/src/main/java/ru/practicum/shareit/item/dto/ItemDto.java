package ru.practicum.shareit.item.dto;

import io.micrometer.core.lang.Nullable;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Value
public class ItemDto {
    @NotEmpty
    String name;

    @NotEmpty
    String description;

    @NotNull
    Boolean available;

    @Nullable
    Long requestId;
}
