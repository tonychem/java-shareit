package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.IncomingItemRequestDto;
import ru.practicum.shareit.requests.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public OutgoingItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody IncomingItemRequestDto incomingItemRequestDto) {
        return itemRequestService.createRequest(userId, incomingItemRequestDto);
    }

    @GetMapping
    public List<OutgoingItemRequestDto> getListOfPersonalRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getListOfPersonalRequests(userId);
    }

    @GetMapping(value = "/all")
    public List<OutgoingItemRequestDto> getListOfRequestsByOthers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                  @RequestParam(name = "from", required = false) Integer from,
                                                                  @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestService.getListOfRequestsByOthers(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    public OutgoingItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        return itemRequestService.requestById(userId, requestId);
    }

}
