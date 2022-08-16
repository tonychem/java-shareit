package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Item {
    private long id;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private long owner;
    private long request;
}
