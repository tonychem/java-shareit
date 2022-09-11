package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutcomingCommentDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemBookingCommentDataDto itemById(long userId, long itemId);

    Collection<ItemBookingCommentDataDto> itemsOfUser(long userId);

    Collection<ItemDto> searchByNameAndDescription(String text);

    OutcomingCommentDto createComment(long userId, long itemId, IncomingCommentDto comment);
}
