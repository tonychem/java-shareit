package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        userStorage.exists(userId);
        return itemStorage.createItem(userId, itemDto);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (itemStorage.itemById(itemId).getOwner() != userId) {
            throw new IllegalStateException("Вы не владелец вещи!");
        }
        return itemStorage.updateItem(itemId, itemDto);
    }

    @Override
    public ItemDto itemById(long itemId) {
        return itemStorage.itemById(itemId);
    }

    @Override
    public Collection<ItemDto> itemsOfUser(long userId) {
        userStorage.exists(userId);
        return itemStorage.itemsOfUser(userId);
    }

    @Override
    public Collection<ItemDto> searchByKeyword(String text) {
        return itemStorage.itemsByKeyword(text);
    }
}
