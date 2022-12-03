package ru.practicum.shareit.user.dto;

import lombok.Value;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;

@Value
public class UserDto {
    long id;

    @Nullable
    String name;

    @Nullable
    @Email
    String email;
}
