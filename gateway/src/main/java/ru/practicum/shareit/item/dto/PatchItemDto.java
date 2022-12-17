package ru.practicum.shareit.item.dto;

import lombok.Value;
import org.springframework.lang.Nullable;

@Value
public class PatchItemDto {
    @Nullable
    String name;

    @Nullable
    String description;

    @Nullable
    Boolean available;
}
