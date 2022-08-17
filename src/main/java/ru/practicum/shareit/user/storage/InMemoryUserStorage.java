package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.exceptions.ConflictingFieldsException;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final UserMapper userMapper;

    private long currentId = 1;

    @Override
    public UserDto userById(long id) {
        User fetchedUser = users.get(id);

        if (fetchedUser == null) {
            throw new NoSuchUserException("Не существует пользователя с id = " + id);
        }

        return userMapper.toUserDto(fetchedUser);
    }

    @Override
    public Collection<UserDto> users() {
        return users.values().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new IllegalStateException("Нельзя создать пользователя без email-a.");
        }

        checkMailAlreadyExists(userDto);

        User userToAdd = userMapper.toUser(userDto);
        userToAdd.setId(currentId);
        users.put(currentId, userToAdd);
        currentId++;
        return userMapper.toUserDto(userToAdd);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        String name;
        String email;

        UserDto currentUserDto = userMapper.toUserDto(users.get(id));

        if (userDto.getEmail() != null) {
            checkMailAlreadyExists(userDto);
            email = userDto.getEmail();
        } else {
            email = currentUserDto.getEmail();
        }

        if (userDto.getName() != null) {
            name = userDto.getName();
        } else {
            name = currentUserDto.getName();
        }

        User updatedUser = new User(id, name, email);
        users.put(id, updatedUser);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public boolean exists(long id) {
        if (users.get(id) == null) {
            throw new NoSuchUserException("Не существует пользователя с id = " + id);
        }
        return true;
    }

    private boolean checkMailAlreadyExists(UserDto userDto) {
        boolean emailExists = users.values().stream()
                .anyMatch(x -> x.getEmail().equals(userDto.getEmail()));
        if (emailExists) {
            throw new ConflictingFieldsException("Такой email уже существует");
        }
        return false;
    }
}
