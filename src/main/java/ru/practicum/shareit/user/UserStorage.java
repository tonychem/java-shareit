package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserStorage {
    UserDto userById(long id);

    Collection<UserDto> users();

    UserDto create(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    void delete(long id);

    boolean exists(long id);
}
