package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto user) {
        return userStorage.create(user);
    }

    @Override
    public UserDto userById(long id) {
        return userStorage.userById(id);
    }

    @Override
    public Collection<UserDto> users() {
        return userStorage.users();
    }

    @Override
    public void deleteUser(long id) {
        userStorage.delete(id);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        if (!userStorage.exists(id)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + id);
        }
        return userStorage.update(id, userDto);
    }
}
