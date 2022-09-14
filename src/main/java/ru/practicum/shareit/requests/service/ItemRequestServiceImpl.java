package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.NoSuchRequestException;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.IncomingItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public OutgoingItemRequestDto createRequest(long userId, IncomingItemRequestDto incomingItemRequestDto) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NoSuchUserException("Не существует пользователя с id = " + userId));
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(incomingItemRequestDto);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(now);
        ItemRequest savedItemRequest = requestRepository.save(itemRequest);
        return itemRequestMapper.toOutgoingItemRequestDto(savedItemRequest, null);
    }

    @Override
    public List<OutgoingItemRequestDto> getListOfPersonalRequests(long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NoSuchUserException("Не существует пользователя с id = " + userId));
        return requestRepository.getItemRequestsByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(x -> {
                    List<ItemDto> items = itemRepository.getItemsByRequestId(x.getId()).stream()
                            .map(itemMapper::toItemDto)
                            .collect(Collectors.toUnmodifiableList());
                    return itemRequestMapper.toOutgoingItemRequestDto(x, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<OutgoingItemRequestDto> getListOfRequestsByOthers(long userId, Integer from, Integer size) {

        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + userId);
        }

        Pageable pageable;

        if (from == null && size == null) {
            pageable = Pageable.unpaged();
        } else {
            pageable = PageRequest.of(from, size);
        }

        return requestRepository.getItemRequestsByRequesterIdIsNotOrderByCreatedDesc(userId, pageable).stream()
                .map(x -> {
                    List<ItemDto> items = itemRepository.getItemsByRequestId(x.getId()).stream()
                            .map(itemMapper::toItemDto)
                            .collect(Collectors.toUnmodifiableList());
                    return itemRequestMapper.toOutgoingItemRequestDto(x, items);
                })
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public OutgoingItemRequestDto requestById(long userId, long requestId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NoSuchUserException("Не существует пользователя с id = " + userId));
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchRequestException("Не существует запроса с id = " + requestId));
        List<ItemDto> itemsForRequest = itemRepository.getItemsByRequestId(requestId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toUnmodifiableList());
        return itemRequestMapper.toOutgoingItemRequestDto(request, itemsForRequest);
    }

}
