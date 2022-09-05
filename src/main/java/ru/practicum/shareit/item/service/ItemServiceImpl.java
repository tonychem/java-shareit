package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.NoSuchItemException;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.item.dto.ItemBookingDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    @Lazy
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
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
    public ItemBookingDataDto itemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NoSuchItemException("Не сущетсвует предмета с id = " + itemId));

        if (item.getOwner().getId() != userId) {
            return itemMapper.toItemBookingDataDto(item, null ,null);
        }

        return getItemWithBookingDate(userId, itemId);
    }

    @Override
    @Transactional
    public Collection<ItemBookingDataDto> itemsOfUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + userId);
        }
        List<Item> itemsOfUser = itemRepository.findItemsOwnedBy(userId);

        return itemsOfUser.stream().map(x -> {return getItemWithBookingDate(userId, x.getId());}).collect(Collectors.toUnmodifiableList());
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

    @Transactional
    public ItemBookingDataDto getItemWithBookingDate(long userId, long itemId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchItemException("Не существует предмета с id = " + itemId));

        if (item.getOwner().getId() != userId) {
            throw new SecurityException("Не владелец вещи!");
        }

        BookingDtoShort previousBooking = bookingMapper.toBookingDtoShort(bookingRepository.getPreviousBooking(itemId, now));
        BookingDtoShort nextBooking = bookingMapper.toBookingDtoShort(bookingRepository.getNextBooking(itemId, now));

        return itemMapper.toItemBookingDataDto(item, previousBooking, nextBooking);
    }
}
