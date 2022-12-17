package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(value = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestBody PatchItemDto itemDto, @PathVariable long itemId,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {
        return itemClient.itemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> items(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
                                        @Positive @RequestParam(value = "size", required = false) Integer size) {
        return itemClient.items(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> itemSearch(@RequestParam("text") String text,
                                             @PositiveOrZero @RequestParam(value = "from", required = false) Integer from,
                                             @Positive @RequestParam(value = "size", required = false) Integer size) {
        return itemClient.itemSearch(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                                @RequestBody @Validated CommentDto comment) {
        return itemClient.createComment(userId, itemId, comment);
    }
}
