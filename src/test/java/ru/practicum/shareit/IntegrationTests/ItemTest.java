package ru.practicum.shareit.IntegrationTests;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
@Transactional
public class ItemTest {
    private final EntityManager em;
    private final MockMvc mockMvc;

    @SneakyThrows
    @Test
    public void findItemsOfUserTest() {
        User owner = new User(0, "owner", "owner@email.com");
        Item item1 = new Item(0, "item1", "desc1", true, owner, null);
        Item item2 = new Item(0, "item2", "desc2", true, owner, null);
        Item item3 = new Item(0, "item3", "desc3", true, owner, null);

        em.persist(owner);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        TypedQuery<User> userTypedQuery = em.createQuery("select u from User u where u.name = :name", User.class);
        long ownerId = userTypedQuery.setParameter("name", owner.getName()).getSingleResult().getId();

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @SneakyThrows
    @Test
    public void shouldFailToRetrieveUnexistingItemById() {
        mockMvc.perform(get("/items/{itemId}", 100)
                .header("X-Sharer-User-Id", 100))
                .andExpect(status().isNotFound());
    }
}
