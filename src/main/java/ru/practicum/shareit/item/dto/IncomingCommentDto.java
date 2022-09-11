package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Value
public class IncomingCommentDto {
    long id;

    @NotNull
    @NotEmpty
    String text;
}
