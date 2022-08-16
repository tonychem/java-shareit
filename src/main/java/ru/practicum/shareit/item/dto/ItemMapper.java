package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;

@Component
@Validated
public class ItemMapper {

    public ItemDto toItemDto(@Valid Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }

    public Item toItem(@Valid ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null) {
            throw new IllegalStateException("...");
        }
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest()
        );
    }
}
