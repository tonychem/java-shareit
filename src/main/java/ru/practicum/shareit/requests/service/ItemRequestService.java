package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.IncomingItemRequestDto;
import ru.practicum.shareit.requests.dto.OutgoingItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    OutgoingItemRequestDto createRequest(long userId, IncomingItemRequestDto incomingItemRequestDto);

    List<OutgoingItemRequestDto> getListOfPersonalRequests(long userId);

    List<OutgoingItemRequestDto> getListOfRequestsByOthers(long userId, Integer from, Integer size);

    OutgoingItemRequestDto requestById(long userId, long requestId);
}
