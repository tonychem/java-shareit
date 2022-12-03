package ru.practicum.shareit.requests.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutgoingItemRequestDto {
    long id;
    String description;
    LocalDateTime created;
    List<ItemDto> items;
}
