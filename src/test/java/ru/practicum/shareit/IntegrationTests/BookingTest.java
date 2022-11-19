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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class BookingTest {

    private final ObjectMapper mapper;
    private final MockMvc mockMvc;
    private final EntityManager em;

    @SneakyThrows
    @Test
    public void createBookingTest() {
        User owner = new User(0, "owner", "owner@email.com");
        User requester = new User(0, "requester", "requester@mail.com");
        Item item = new Item(0, "item", "desc", true, owner, null);

        em.persist(owner);
        em.persist(requester);
        em.persist(item);
        em.flush();

        TypedQuery<User> queryForRequester = em.createQuery("select u from User u where u.name = :name", User.class);
        long requesterId = queryForRequester.setParameter("name", requester.getName()).getSingleResult().getId();

        TypedQuery<Item> queryForItem = em.createQuery("select i from Item i where i.name = :name", Item.class);
        long itemId = queryForItem.setParameter("name", item.getName()).getSingleResult().getId();

        LocalDateTime start = LocalDateTime.now().plusDays(10);
        LocalDateTime end = start.plusDays(20);
        DateTimeFormatter excludingMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        BookingDto dto = new BookingDto(0, start, end, null, null, null, itemId, 0);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookerId", is((int) requesterId)))
                .andExpect(jsonPath("$.start", containsString(start.format(excludingMillis))))
                .andExpect(jsonPath("$.end", containsString(end.format(excludingMillis))));

        TypedQuery<Booking> queryForBooking = em.createQuery("select b from Booking b where b.booker = :booker", Booking.class);
        Booking booking = queryForBooking.setParameter("booker", requester).getSingleResult();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @SneakyThrows
    @Test
    public void getListOfBookingsOfBookerByStatusTest() {
        User owner = new User(0, "owner", "owner@email.com");
        User requester = new User(0, "requester", "requester@mail.com");
        Item itemForPastBooking = new Item(0, "itemPastBooking", "desc", true, owner, null);
        Item itemForCurrentBooking = new Item(0, "itemCurrentBooking", "desc", true, owner, null);
        Item itemForFutureBooking = new Item(0, "itemFutureBooking", "desc", true, owner, null);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyDaysBeforeNow = now.minusDays(20);
        LocalDateTime tenDaysBeforeNow = now.minusDays(10);
        LocalDateTime tenDaysAfterNow = now.plusDays(10);
        LocalDateTime twentyDaysAfterNow = now.plusDays(20);

        DateTimeFormatter excludingMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        Booking bookingInPast = new Booking(0, twentyDaysBeforeNow, tenDaysBeforeNow, itemForPastBooking, requester, BookingStatus.APPROVED);
        Booking currentBooking = new Booking(0, tenDaysBeforeNow, tenDaysAfterNow, itemForCurrentBooking, requester, BookingStatus.APPROVED);
        Booking bookingInFuture = new Booking(0, tenDaysAfterNow, twentyDaysAfterNow, itemForFutureBooking, requester, BookingStatus.APPROVED);

        em.persist(owner);
        em.persist(requester);
        em.persist(itemForPastBooking);
        em.persist(itemForCurrentBooking);
        em.persist(itemForFutureBooking);
        em.persist(bookingInPast);
        em.persist(currentBooking);
        em.persist(bookingInFuture);
        em.flush();

        TypedQuery<User> queryForRequester = em.createQuery("select u from User u where u.name = :name", User.class);
        long requesterId = queryForRequester.setParameter("name", requester.getName()).getSingleResult().getId();

        TypedQuery<Item> queryForItem = em.createQuery("select i from Item i where i.name = :name", Item.class);
        long itemInPastId = queryForItem.setParameter("name", itemForPastBooking.getName()).getSingleResult().getId();
        long currentItemId = queryForItem.setParameter("name", itemForCurrentBooking.getName()).getSingleResult().getId();
        long itemInFutureId = queryForItem.setParameter("name", itemForFutureBooking.getName()).getSingleResult().getId();

        //current bookings
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", requesterId)
                        .param("state", "current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookerId", is((int) requesterId)))
                .andExpect(jsonPath("$[0].itemId", is((int) currentItemId)));

        //past bookings
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", requesterId)
                        .param("state", "past"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookerId", is((int) requesterId)))
                .andExpect(jsonPath("$[0].itemId", is((int) itemInPastId)));

        //future bookings
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", requesterId)
                        .param("state", "future"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookerId", is((int) requesterId)))
                .andExpect(jsonPath("$[0].itemId", is((int) itemInFutureId)));

        //all
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", requesterId)
                        .param("state", "all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId", is((int) itemInFutureId)))
                .andExpect(jsonPath("$[1].itemId", is((int) currentItemId)))
                .andExpect(jsonPath("$[2].itemId", is((int) itemInPastId)));

        //rejected -> none
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", requesterId)
                        .param("state", "rejected"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @SneakyThrows
    @Test
    public void shouldFailToRetrieveUnexistingBooking() {
        mockMvc.perform(get("/bookings/{bookingId}", 100)
                .header("X-Sharer-User-Id", 100))
                .andExpect(status().isNotFound());
    }
}
