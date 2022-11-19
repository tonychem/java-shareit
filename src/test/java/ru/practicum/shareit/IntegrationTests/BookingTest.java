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
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @SneakyThrows
    @Test
    public void shouldSetApprovedBookingStatus() {
        User owner = new User(0, "owner", "owner@email.com");
        User requester = new User(0, "requester", "requester@email.com");

        Item item = new Item(0, "item", "description", true, owner, null);
        Booking booking = new Booking(0, LocalDateTime.now(), LocalDateTime.now().plusDays(10), item, requester,
                BookingStatus.WAITING);

        em.persist(owner);
        em.persist(requester);
        em.persist(item);
        em.persist(booking);
        em.flush();

        TypedQuery<User> userTypedQuery = em.createQuery("select u from User u where u.name = :name", User.class);
        TypedQuery<Booking> bookingTypedQuery = em.createQuery("select b from Booking b where b.item = :item",
                Booking.class);
        TypedQuery<Item> itemTypedQuery = em.createQuery("select i from Item i where i.name = :name", Item.class);

        long itemId = itemTypedQuery.setParameter("name", item.getName()).getSingleResult().getId();
        long ownerId = userTypedQuery.setParameter("name", owner.getName()).getSingleResult().getId();
        long requesterId = userTypedQuery.setParameter("name", requester.getName()).getSingleResult().getId();
        long bookingId = bookingTypedQuery.setParameter("item", item).getSingleResult().getId();

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is("APPROVED")))
                .andExpect(jsonPath("itemId", is((int) itemId)))
                .andExpect(jsonPath("bookerId", is((int) requesterId)));
    }

    @SneakyThrows
    @Test
    public void shouldThrowExceptionWhenSettingStatusForApprovedBooking() {
        User owner = new User(0, "owner", "owner@email.com");
        User requester = new User(0, "requester", "requester@email.com");

        Item item = new Item(0, "item", "description", true, owner, null);
        Booking booking = new Booking(0, LocalDateTime.now(), LocalDateTime.now().plusDays(10), item, requester,
                BookingStatus.APPROVED);

        em.persist(owner);
        em.persist(requester);
        em.persist(item);
        em.persist(booking);
        em.flush();

        TypedQuery<User> userTypedQuery = em.createQuery("select u from User u where u.name = :name", User.class);
        TypedQuery<Booking> bookingTypedQuery = em.createQuery("select b from Booking b where b.item = :item",
                Booking.class);

        long ownerId = userTypedQuery.setParameter("name", owner.getName()).getSingleResult().getId();
        long bookingId = bookingTypedQuery.setParameter("item", item).getSingleResult().getId();

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void shouldReturnListOfBookingsWhereItemsAreOwnedByUser() {
        User owner = new User(0, "owner", "owner@email.com");
        User requester = new User(0, "requester", "requester@email.com");

        Item itemForPastBooking = new Item(0, "past", "description", true, owner, null);
        Item itemForCurrentBooking = new Item(0, "current", "description", true, owner, null);
        Item itemForFutureBooking = new Item(0, "future", "description", true, owner, null);
        Item itemForWaiting = new Item(0, "waiting", "description", true, owner, null);
        Item itemForRejected = new Item(0, "rejected", "description", true, owner, null);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime daysAgo10 = now.minusDays(10);
        LocalDateTime daysAgo5 = now.minusDays(5);
        LocalDateTime daysAgo2 = now.minusDays(2);
        LocalDateTime in2Days = now.plusDays(2);
        LocalDateTime in5Days = now.plusDays(5);
        LocalDateTime in10Days = now.plusDays(10);

        Booking pastBookingApproved = new Booking(0, daysAgo10, daysAgo5, itemForPastBooking, requester,
                BookingStatus.APPROVED);
        Booking currentBookingApproved = new Booking(0, daysAgo5, in5Days, itemForCurrentBooking, requester,
                BookingStatus.APPROVED);
        Booking futureBookingApproved = new Booking(0, in5Days, in10Days, itemForFutureBooking, requester,
                BookingStatus.APPROVED);

        Booking futureBookingWaiting = new Booking(0, in2Days, in5Days, itemForWaiting, requester,
                BookingStatus.WAITING);
        Booking pastBookingRejected = new Booking(0, daysAgo5, daysAgo2, itemForRejected, requester,
                BookingStatus.REJECTED);

        em.persist(owner);
        em.persist(requester);
        em.persist(itemForPastBooking);
        em.persist(itemForCurrentBooking);
        em.persist(itemForFutureBooking);
        em.persist(itemForWaiting);
        em.persist(itemForRejected);
        em.persist(pastBookingApproved);
        em.persist(currentBookingApproved);
        em.persist(futureBookingApproved);
        em.persist(futureBookingWaiting);
        em.persist(pastBookingRejected);
        em.flush();

        TypedQuery<User> userTypedQuery = em.createQuery("select u from User u where u.name = :name", User.class);
        TypedQuery<Item> itemTypedQuery = em.createQuery("select i from Item i where i.name = :name", Item.class);

        long ownerId = userTypedQuery.setParameter("name", owner.getName()).getSingleResult().getId();
        long itemForPastBookingId = itemTypedQuery.setParameter("name", itemForPastBooking.getName())
                .getSingleResult().getId();
        long itemForCurrentBookingId = itemTypedQuery.setParameter("name", itemForCurrentBooking.getName())
                .getSingleResult().getId();
        long itemForFutureBookingId = itemTypedQuery.setParameter("name", itemForFutureBooking.getName())
                .getSingleResult().getId();
        long itemForWaitingId = itemTypedQuery.setParameter("name", itemForWaiting.getName())
                .getSingleResult().getId();
        long itemForRejectedId = itemTypedQuery.setParameter("name", itemForRejected.getName())
                .getSingleResult().getId();

        //текущие бронирования
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId", is((int) itemForCurrentBookingId)))
                .andExpect(jsonPath("$[0].status", is("APPROVED")));

        //прошлые бронирования, отсортир по дате окончания в порядке убывания
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "past"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].itemId", is((int) itemForRejectedId)))
                .andExpect(jsonPath("$[1].itemId", is((int) itemForPastBookingId)));

        //будущие бронирования, отсортир по дате окончания в порядке убывания
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "future"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].itemId", is((int) itemForFutureBookingId)))
                .andExpect(jsonPath("$[1].itemId", is((int) itemForWaitingId)));

        //бронирование со статусом WAITING
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "waiting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId", is((int) itemForWaitingId)));

        //бронирование со статусом REJECTED
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "rejected"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId", is((int) itemForRejectedId)));
    }

    @SneakyThrows
    @Test
    public void shouldFailWhenStateIsUnknown() {
        User user = new User(0, "user", "user@email.com");

        em.persist(user);
        em.flush();

        TypedQuery<User> userTypedQuery = em.createQuery("select u from User u where u.name = :name", User.class);
        long userId = userTypedQuery.setParameter("name", user.getName()).getSingleResult().getId();

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "unknown"))
                .andExpect(status().isBadRequest());
    }
}
