package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }

    public Item toItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null) {
            throw new IllegalStateException("Попытка создать объект Item с null-полями.");
        }
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest()
        );
    }

    public ItemBookingCommentDataDto toItemBookingCommentDataDto(Item item, BookingDtoShort lastBooking, BookingDtoShort nextBooking, List<OutcomingCommentDto> listOfOutcomingCommentDto) {
        return new ItemBookingCommentDataDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getOwner(), lastBooking, nextBooking, listOfOutcomingCommentDto);
    }
}
