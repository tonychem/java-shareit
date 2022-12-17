package ru.practicum.shareit.user.dto;


import lombok.Value;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;

@Value
public class UserDto {
    @Nullable
    @Email
    String email;

    @Nullable
    String name;
}
