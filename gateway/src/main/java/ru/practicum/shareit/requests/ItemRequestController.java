package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.IncomingItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(value = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody IncomingItemRequestDto incomingItemRequestDto) {
        return itemRequestClient.createRequest(userId, incomingItemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getListOfPersonalRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.listOfPersonalRequests(userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> getListOfRequestsByOthers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                                            @Positive @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestClient.listOfRequestsByOthers(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        return itemRequestClient.itemRequestById(userId, requestId);
    }
}
