package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemBookingDataDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemBookingDataDto itemById(long userId, long itemId);

    Collection<ItemBookingDataDto> itemsOfUser(long userId);

    Collection<ItemDto> searchByNameAndDescription(String text);

}
