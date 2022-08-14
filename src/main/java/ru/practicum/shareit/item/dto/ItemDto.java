package ru.practicum.shareit.item.dto;

import lombok.Value;

@Value
public class ItemDto {
    long id;
    String name;
    String description;
    boolean available;
    long owner;
    long request;
}
