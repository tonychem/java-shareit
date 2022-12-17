package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String url) {
        super(url + API_PREFIX);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("/", userDto);
    }

    public ResponseEntity<Object> updateUser(long userId, UserDto userDto) {
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> users() {
        return get("/");
    }

    public ResponseEntity<Object> userById(long userId) {
        return get("/" + userId);
    }

    public void deleteUser(long userId) {
        delete("/" + userId);
    }
}
