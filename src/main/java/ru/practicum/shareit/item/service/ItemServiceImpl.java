package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptions.NoSuchItemException;
import ru.practicum.shareit.exception.exceptions.NoSuchRequestException;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

    private final RequestRepository requestRepository;
    @Lazy
    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        User creator = userRepository.findById(userId).orElseThrow(() -> new NoSuchUserException("Не существует пользователя с id = " + userId));
        ItemRequest request = null;

        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NoSuchRequestException("Не существует запроса с id = " + itemDto.getRequestId()));
        }

        Item itemToBeSaved = itemMapper.toItem(itemDto);
        itemToBeSaved.setOwner(creator);
        itemToBeSaved.setRequest(request);

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
    public ItemBookingCommentDataDto itemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchItemException("Не существует предмета с id = " + itemId));
        List<OutcomingCommentDto> comments = commentRepository.findCommentsByItemId(itemId).stream()
                .map(commentMapper::toOutcomingCommentDto)
                .collect(Collectors.toUnmodifiableList());

        if (item.getOwner().getId() != userId) {
            return itemMapper.toItemBookingCommentDataDto(item, null, null, comments);
        }

        return getItemWithBookingDateAndComment(userId, itemId);
    }

    @Override
    public Collection<ItemBookingCommentDataDto> itemsOfUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + userId);
        }
        List<Item> itemsOfUser = itemRepository.findItemsOwnedBy(userId);

        return itemsOfUser.stream().map(x -> getItemWithBookingDateAndComment(userId, x.getId())).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Collection<ItemDto> searchByNameAndDescription(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> found = itemRepository.search(text);
        return found.stream().filter(Item::getAvailable).map(itemMapper::toItemDto).collect(Collectors.toUnmodifiableList());
    }

    public ItemBookingCommentDataDto getItemWithBookingDateAndComment(long userId, long itemId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchItemException("Не существует предмета с id = " + itemId));

        if (item.getOwner().getId() != userId) {
            throw new SecurityException("Не владелец вещи!");
        }

        BookingDtoShort previousBooking = bookingMapper.toBookingDtoShort(bookingRepository.getPreviousBooking(itemId, now));
        BookingDtoShort nextBooking = bookingMapper.toBookingDtoShort(bookingRepository.getNextBooking(itemId, now));
        List<OutcomingCommentDto> comments = commentRepository.findCommentsByItemId(itemId).stream().map(commentMapper::toOutcomingCommentDto).collect(Collectors.toUnmodifiableList());

        return itemMapper.toItemBookingCommentDataDto(item, previousBooking, nextBooking, comments);
    }

    @Override
    @Transactional
    public OutcomingCommentDto createComment(long userId, long itemId, IncomingCommentDto incomingCommentDto) {
        User tenant = userRepository.findById(userId).orElseThrow(() -> new NoSuchUserException("Не существует пользователя с id = " + userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchItemException("Не существует вещи с id = " + itemId));

        List<Booking> bookingsByUser = bookingRepository.findByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());

        if (bookingsByUser.isEmpty()) {
            throw new IllegalStateException("Пользователь не найден в арендаторах вещи");
        }

        Comment comment = commentMapper.toComment(incomingCommentDto);
        comment.setAuthor(tenant);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toOutcomingCommentDto(savedComment);
    }
}
