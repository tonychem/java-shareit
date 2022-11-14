package ru.practicum.shareit.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Transactional
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserTest {
    private final EntityManager em;
    private final ObjectMapper mapper;
    private final MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void shouldSaveAndRetrieveUser() {
        UserDto userDto = new UserDto(0, "name", "email@email.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        TypedQuery<User> userTypedQuery = em.createQuery("select u from User u where u.name = :name", User.class);
        User savedUserFromDB = userTypedQuery.setParameter("name", userDto.getName()).getSingleResult();

        mockMvc.perform(get("/users/{userId}", savedUserFromDB.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(savedUserFromDB.getName())))
                .andExpect(jsonPath("$.email", is(savedUserFromDB.getEmail())));
    }
}
