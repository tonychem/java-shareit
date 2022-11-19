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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.IncomingItemRequestDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
@Transactional
public class ItemRequestTest {
    private final ObjectMapper mapper;
    private final EntityManager em;
    private final MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void shouldCreateAndReturnItemRequests() {
        User owner = new User(0, "owner", "owner@email.com");
        User requester = new User(0, "requester", "requester@mail.com");
        Item itemForRequest = new Item(0, "item", "desc", true, owner, null);
        Item distractingItem = new Item(0, "item", "distractor", true, owner, null);

        em.persist(owner);
        em.persist(requester);
        em.persist(itemForRequest);
        em.persist(distractingItem);
        em.flush();

        IncomingItemRequestDto requestDto = new IncomingItemRequestDto("desc");

        TypedQuery<User> userTypedQuery = em.createQuery("select u from User u where u.name = :name", User.class);
        long requesterId = userTypedQuery.setParameter("name", requester.getName()).getSingleResult().getId();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemForRequest.getDescription())));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @SneakyThrows
    public void shouldFailToRetrieveUnexistingRequestById() {
        User user = new User(0, "name", "email@email.com");
        em.persist(user);

        TypedQuery<User> userTypedQuery = em.createQuery("select u from User u where u.name = :name", User.class);
        long userId = userTypedQuery.setParameter("name", user.getName()).getSingleResult().getId();

        mockMvc.perform(get("/requests/{requestId}", 100)
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }
}
