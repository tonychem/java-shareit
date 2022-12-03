package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String url) {
        super(url + API_PREFIX);
    }

    public ResponseEntity<Object> createItem(ItemDto itemDto, long userId) {
        return post("/", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(PatchItemDto patchItemDto, long itemId, long userId) {
        return patch("/" + itemId, userId, patchItemDto);
    }

    public ResponseEntity<Object> itemById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> items(long userId, Integer from, Integer size) {
        if (from == null || size == null) {
            return get("/", userId);
        }

        Map<String, Object> params = Map.of("from", from,
                "size", size);
        return get("/?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> itemSearch(String text, Integer from, Integer size) {
        if (from == null || size == null) {
            return get("/search?text=" + text);
        }

        Map<String, Object> params = Map.of("from", from,
                "size", size,
                "text", text);
        return get("/search?text={text}&from={from}&size={size}");
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
