package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final UserMapper mapper;

    private long currentId = 1;

    @Override
    public UserDto userById(long id) {
        User fetchedUser = users.get(id);

        if (fetchedUser == null) {
            throw new NoSuchUserException("Не существует пользователя с id = " + id);
        }

        return mapper.toUserDto(fetchedUser);
    }

    @Override
    public Collection<UserDto> users() {
        return users.values().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public UserDto create(UserDto userDto) {

        if (userDto.getEmail() != null) {
            boolean uniqueEmail = users.values().stream()
                    .noneMatch(x -> x.getEmail().equals(userDto.getEmail()));
            if (!uniqueEmail) {
                throw new ConflictingFieldsException("Пользователь с email-ом " + userDto.getEmail() + " уже существует");
            }
        } else {
            throw new IllegalStateException("Нельзя создать пользователя без email-a.");
        }

        User userToAdd = mapper.toUser(userDto);
        userToAdd.setId(currentId);
        users.put(currentId, userToAdd);
        currentId++;
        return mapper.toUserDto(userToAdd);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        String name;
        String email;

        UserDto currentUserDto = mapper.toUserDto(users.get(id));

        if (userDto.getEmail() != null ) {
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
        return mapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public boolean exists(long id) {
        return users.get(id) != null;
    }

    private boolean checkMailAlreadyExists(UserDto userDto) {
        boolean emailExists = users.values().stream()
                .anyMatch(x -> x.getEmail().equals(userDto.getEmail()));
        if (emailExists) {
            throw new IllegalStateException("Такой email уже существует");
        }
        return false;
    }
}
