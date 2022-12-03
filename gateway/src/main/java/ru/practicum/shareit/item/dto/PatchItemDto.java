package ru.practicum.shareit.item.dto;

import io.micrometer.core.lang.Nullable;
import lombok.Value;

@Value
public class PatchItemDto {
    @Nullable
    String name;

    @Nullable
    String description;

    @Nullable
    Boolean available;
}
