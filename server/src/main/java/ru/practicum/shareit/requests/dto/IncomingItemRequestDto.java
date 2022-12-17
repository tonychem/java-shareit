package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;


@Value
@NoArgsConstructor
@AllArgsConstructor
public class IncomingItemRequestDto {
    @NonFinal
    String description;
}
