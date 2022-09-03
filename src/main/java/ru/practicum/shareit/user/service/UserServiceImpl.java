package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserDto user) {
        User userToCreate = userMapper.toUser(user);
        User userAdded = userRepository.save(userToCreate);
        return userMapper.toUserDto(userAdded);
    }

    @Override
    @Transactional
    public UserDto userById(long id) {
        Optional<User> fetchedUser = userRepository.findById(id);
        UserDto userDto = userMapper.toUserDto(fetchedUser.orElseThrow(() ->
                new NoSuchUserException("Не существует пользователя с id = " + id)));
        return userDto;
    }

    @Override
    @Transactional
    public Collection<UserDto> users() {
        Collection<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserDto).collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        User userFromDB = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("Не существует пользователя с id = " + id));

        if (userDto.getEmail() != null) {
            userFromDB.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            userFromDB.setName(userDto.getName());
        }

        User userSaved = userRepository.save(userFromDB);
        return userMapper.toUserDto(userSaved);
    }
}
