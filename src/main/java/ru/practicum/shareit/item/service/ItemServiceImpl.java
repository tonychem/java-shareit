package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.NoSuchItemException;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + userId);
        }

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("Не существует пользователя с id = " + userId));

        Item itemToBeSaved = itemMapper.toItem(itemDto);
        itemToBeSaved.setOwner(creator);

        Item savedItem = itemRepository.save(itemToBeSaved);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + userId);
        }

        if (itemRepository.existsById(itemId) && itemRepository.findById(itemId).get().getOwner().getId() != userId) {
            throw new SecurityException("Пользователь (id=" + userId + ") не является владельцем вещи (id=" + itemId + ").");
        }

        Item itemFromDb = itemRepository.findById(itemId).get();

        if (itemDto.getName() != null) {
            itemFromDb.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            itemFromDb.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemFromDb.setAvailable(itemDto.getAvailable());
        }

        Item itemSaved = itemRepository.save(itemFromDb);

        return itemMapper.toItemDto(itemSaved);
    }

    @Override
    public ItemDto itemById(long itemId) {
        ItemDto itemDtoToReturn = itemRepository.findById(itemId).map(itemMapper::toItemDto).orElseThrow(
                () -> new NoSuchItemException("Не сущетсвует предмета с id = " + itemId));
        return itemDtoToReturn;
    }

    @Override
    @Transactional
    public Collection<ItemDto> itemsOfUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + userId);
        }
        List<Item> itemsOfUser = itemRepository.findItemsOwnedBy(userId);

        return itemsOfUser.stream().map(itemMapper::toItemDto).collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional
    public Collection<ItemDto> searchByNameAndDescription(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> found = itemRepository.search(text);
        return found.stream().filter(Item::getAvailable).map(itemMapper::toItemDto).collect(Collectors.toUnmodifiableList());
    }
}
