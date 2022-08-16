package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.exceptions.NoSuchItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final ItemMapper itemMapper;
    private final Map<Long, Item> items = new HashMap<>();
    private long currentId = 1;
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        Item itemToAdd = itemMapper.toItem(itemDto);
        itemToAdd.setOwner(userId);
        itemToAdd.setId(currentId);
        items.put(currentId, itemToAdd);
        currentId++;
        return itemMapper.toItemDto(itemToAdd);
    }

    @Override
    public ItemDto itemById(long itemId) {
        exists(itemId);
        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto) {
        exists(itemId);

        String name;
        String description;
        Boolean status;

        ItemDto currentItemInMemory = itemMapper.toItemDto(items.get(itemId));

        if (itemDto.getName() != null) {
            name = itemDto.getName();
        } else {
            name = currentItemInMemory.getName();
        }

        if (itemDto.getDescription() != null) {
            description = itemDto.getDescription();
        } else {
            description = currentItemInMemory.getDescription();
        }

        if (itemDto.getAvailable() != null) {
            status = itemDto.getAvailable();
        } else {
            status = currentItemInMemory.getAvailable();
        }

        Item updatedItem = new Item(currentItemInMemory.getId(), name, description, status, currentItemInMemory.getOwner(),
                currentItemInMemory.getRequest());

        items.put(currentItemInMemory.getId(), updatedItem);

        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public Collection<ItemDto> itemsOfUser(long userId) {
        return items.values().stream()
                .filter(x -> x.getOwner() == userId)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> itemsByKeyword(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(x -> {
                    String nameLowered = x.getName().toLowerCase();
                    String descriptionLowered = x.getDescription().toLowerCase();
                    String searchTextLowered = text.toLowerCase();

                    return nameLowered.contains(searchTextLowered) || descriptionLowered.contains(searchTextLowered);
                })
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(long itemId) {
        if (items.get(itemId) == null) {
            throw new NoSuchItemException("Не существует вещи с id = " + itemId);
        }
        return true;
    }
}
