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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

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
public class ItemTest {
    private final EntityManager em;
    private final MockMvc mockMvc;

    private final ObjectMapper mapper;

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

    @SneakyThrows
    @Test
    public void shouldCreateItem() {
        User user = new User(0, "user", "user@email.com");
        em.persist(user);

        TypedQuery<User> userTypedQuery = em.createQuery("select u from User u where u.name = :name", User.class);
        long userId = userTypedQuery.setParameter("name", user.getName()).getSingleResult().getId();

        ItemDto dto = new ItemDto(0, "itemName", "itemDescription", true, null,
                null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner.id", is((int) userId)));
    }

    @SneakyThrows
    @Test
    public void shouldCreateComment() {
        User owner = new User(0, "owner", "owner@email.com");
        Item item = new Item(0, "item", "itemDescription", true, owner, null);
        Booking booking = new Booking(0, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5),
                item, owner, BookingStatus.APPROVED);

        em.persist(owner);
        em.persist(item);
        em.persist(booking);
        em.flush();

        TypedQuery<User> userTypedQuery = em.createQuery("select u from User u where u.name = :name", User.class);
        TypedQuery<Item> itemTypedQuery = em.createQuery("select i from Item i where i.name = :name", Item.class);

        long ownerId = userTypedQuery.setParameter("name", owner.getName()).getSingleResult().getId();
        long itemId = itemTypedQuery.setParameter("name", item.getName()).getSingleResult().getId();

        IncomingCommentDto dto = new IncomingCommentDto(0, "comment text");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(dto.getText())))
                .andExpect(jsonPath("$.authorName", is(owner.getName())));
    }
}
