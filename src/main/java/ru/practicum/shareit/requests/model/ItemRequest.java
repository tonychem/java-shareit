package ru.practicum.shareit.requests.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private long id;
    private String description;
    private long requester;
    private LocalDateTime created;
}
