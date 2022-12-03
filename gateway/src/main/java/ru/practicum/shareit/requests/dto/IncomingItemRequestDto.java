package ru.practicum.shareit.requests.dto;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;

@Value
@NoArgsConstructor
public class IncomingItemRequestDto {
    @NonFinal
    @NotEmpty
    String description;
}
