package ru.practicum.shareit.item.dto;

import lombok.Value;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Value
public class ItemDto {
    @Nullable
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
    long owner;
    @Nullable
    long request;
}
