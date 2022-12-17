package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.IncomingItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String url) {
        super(url + API_PREFIX);
    }

    public ResponseEntity<Object> createRequest(long userId, IncomingItemRequestDto incomingItemRequestDto) {
        return post("/", userId, incomingItemRequestDto);
    }

    public ResponseEntity<Object> listOfPersonalRequests(long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> listOfRequestsByOthers(long userId, Integer from, Integer size) {
        if (from == null || size == null) {
            return get("/all", userId);
        }

        Map<String, Object> params = Map.of("from", from,
                "size", size);
        return get("/all?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> itemRequestById(long userId, long requestId) {
        return get("/" + requestId, userId);
    }
}
