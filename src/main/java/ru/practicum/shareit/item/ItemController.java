package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemBookingDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto, @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDataDto getItem(@RequestHeader(value = "X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemService.itemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemBookingDataDto> items(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemService.itemsOfUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> itemSearch(@RequestParam("text") String text) {
        return itemService.searchByNameAndDescription(text);
    }
}
