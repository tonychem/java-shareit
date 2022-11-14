package ru.practicum.shareit.requests.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
