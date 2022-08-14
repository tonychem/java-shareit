package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long owner;
    private long request;
}
