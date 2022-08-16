package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto itemById(long itemId);

    Collection<ItemDto> itemsOfUser(long userId);

    Collection<ItemDto> searchByKeyword(String text);
}
