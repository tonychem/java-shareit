package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
public class IncomingCommentDto {
    long id;

    @NotEmpty
    String text;
}
