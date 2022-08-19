package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        userStorage.checkExists(userId);
        return itemStorage.createItem(userId, itemDto);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        itemStorage.checkExists(itemId);

        if (itemStorage.itemById(itemId).getOwner().getId() != userId) {
            throw new SecurityException("Пользователь (id=" + userId + ") не является владельцем вещи (id=" + itemId + ").");
        }

        return itemStorage.updateItem(itemId, itemDto);
    }

    @Override
    public ItemDto itemById(long itemId) {
        itemStorage.checkExists(itemId);
        return itemStorage.itemById(itemId);
    }

    @Override
    public Collection<ItemDto> itemsOfUser(long userId) {
        userStorage.checkExists(userId);

        return itemStorage.items().stream()
                .filter(x -> x.getOwner().getId() == userId)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Collection<ItemDto> searchByNameAndDescription(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.items().stream()
                .filter(x -> {
                    String nameLowered = Optional.ofNullable(x.getName()).map(String::toLowerCase).orElse("");
                    String descriptionLowered = Optional.ofNullable(x.getDescription()).map(String::toLowerCase).orElse("");
                    String searchTextLowered = text.toLowerCase();

                    return nameLowered.contains(searchTextLowered) || descriptionLowered.contains(searchTextLowered);
                })
                .filter(x -> x.getAvailable() != null && x.getAvailable())
                .collect(Collectors.toList());
    }
}
