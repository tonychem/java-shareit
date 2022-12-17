package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemStorage {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto itemById(long itemId);

    ItemDto updateItem(long itemId, ItemDto itemDto);

    boolean checkExists(long itemId);

    Collection<ItemDto> items();
}
