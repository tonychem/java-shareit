package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @RequestBody @Valid UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> allUsers() {
        return userClient.users();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> userById(@PathVariable long userId) {
        return userClient.userById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userClient.deleteUser(userId);
    }
}
