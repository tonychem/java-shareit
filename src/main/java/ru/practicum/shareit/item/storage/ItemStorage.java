package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.awt.*;
import java.util.Collection;

public interface ItemStorage {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto itemById(long itemId);

    ItemDto updateItem(long itemId, ItemDto itemDto);

    Collection<ItemDto> itemsOfUser(long userId);

    Collection<ItemDto> itemsByKeyword(String text);

    boolean exists(long itemId);
}
