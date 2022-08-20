package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto user);

    UserDto userById(long id);

    Collection<UserDto> users();

    void deleteUser(long id);

    UserDto updateUser(long id, UserDto userDto);
}
