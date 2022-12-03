package ru.practicum.shareit.requests.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

@Component
public class ItemRequestMapper {
    public OutgoingItemRequestDto toOutgoingItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new OutgoingItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), items);
    }

    public ItemRequest toItemRequest(IncomingItemRequestDto incomingItemRequestDto) {
        return new ItemRequest(0, incomingItemRequestDto.getDescription(), null, null);
    }
}
