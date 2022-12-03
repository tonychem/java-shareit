package ru.practicum.shareit.MVCTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private final UserDto savedValidUser1 = new UserDto(1, "Valid1", "email1@email.com");
    private final UserDto validUser1 = new UserDto(0, "Valid1", "email1@email.com");
    private final UserDto invalidEmailUser = new UserDto(0, "Invalid", "mail.com");
    private final UserDto validUserData = new UserDto(0, null, "update@email.com");

    @Test
    public void shouldCreateValidUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(new UserDto(1, "Valid1", "email1@email.com"));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(validUser1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Valid1")))
                .andExpect(jsonPath("$.email", is("email1@email.com")));
    }

    @Disabled("Тест не имеет смысла после переноса валидации в gateway")
    @Test
    public void shouldReturn400WhenCreatingUserWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidEmailUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnExistingUserById() throws Exception {
        when(userService.userById(1))
                .thenReturn(savedValidUser1);

        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Valid1")))
                .andExpect(jsonPath("$.email", is("email1@email.com")));
    }

    @Test
    public void shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.userById(2))
                .thenThrow(NoSuchUserException.class);

        mockMvc.perform(get("/users/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Disabled("Тест не имеет смысла после переноса валидации в gateway")
    @Test
    public void shouldReturn400WhenPatchingWithInvalidEmailUser() throws Exception {
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidEmailUser)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldUpdateUserWithValidUserData() throws Exception {
        when(userService.updateUser(1, validUserData))
                .thenReturn(new UserDto(1, "Valid1", "update@email.com"));

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validUserData)))
                .andExpect(jsonPath("$.email", is("update@email.com")));
    }

}
