package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;

@Value
@NoArgsConstructor
public class CommentDto {
    @NonFinal
    @NotEmpty
    String text;
}
