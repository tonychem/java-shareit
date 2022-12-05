package ru.practicum.shareit.user.dto;

import lombok.Value;
import org.springframework.lang.Nullable;

@Value
public class UserDto {
    long id;

    @Nullable
    String name;

    @Nullable
    String email;
}
