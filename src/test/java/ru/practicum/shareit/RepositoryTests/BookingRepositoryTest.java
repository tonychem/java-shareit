package ru.practicum.shareit.RepositoryTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager em;
    private static LocalDateTime past1 = LocalDateTime.of(1999, 1, 1, 1, 1, 1);
    ;
    private static LocalDateTime past2 = LocalDateTime.of(2000, 1, 1, 1, 1, 1);
    private static LocalDateTime future1 = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
    private static LocalDateTime future2 = LocalDateTime.of(2031, 1, 1, 1, 1, 1);

    @Test
    public void shouldFindBookingsByBookerAndStatusOrdered() {
        User booker = new User(0, "booker", "mail1@email.com");
        Item item1 = new Item(0, "item1", "desc1", true, booker, null);
        Item item2 = new Item(0, "item2", "desc2", true, booker, null);
        Item item3 = new Item(0, "item3", "desc3", true, booker, null);

        Booking bookingInPast = new Booking(0, past1, past2, item1, booker, BookingStatus.APPROVED);
        Booking bookingCurrent = new Booking(0, past2, future1, item2, booker, BookingStatus.APPROVED);
        Booking bookingInFuture = new Booking(0, future1, future2, item3, booker, BookingStatus.APPROVED);

        long bookerId = em.persist(booker).getId();
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.persist(bookingInPast);
        em.persist(bookingCurrent);
        em.persist(bookingInFuture);
        em.flush();

        List<Booking> listOf3Bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(bookerId, BookingStatus.APPROVED, Pageable.unpaged());
        List<Booking> noBookingList = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(bookerId, BookingStatus.WAITING, Pageable.unpaged());

        assertThat(listOf3Bookings).hasSize(3);
        assertThat(noBookingList).hasSize(0);
        //Убедится, что список бронирований в порядке уменьшения по дате окончания
        assertThat(listOf3Bookings.get(0).getEnd()).isEqualToIgnoringHours(future2);
    }

    @Test
    public void shouldFind2BookingsByEndBeforeOrdered() {
        User booker = new User(0, "booker", "mail1@email.com");
        Item item1 = new Item(0, "item1", "desc1", true, booker, null);
        Item item2 = new Item(0, "item2", "desc2", true, booker, null);
        Item item3 = new Item(0, "item3", "desc3", true, booker, null);

        Booking bookingInPast = new Booking(0, past1, past2, item1, booker, BookingStatus.APPROVED);
        Booking bookingCurrent = new Booking(0, past2, future1, item2, booker, BookingStatus.APPROVED);
        Booking bookingInFuture = new Booking(0, future1, future2, item3, booker, BookingStatus.APPROVED);

        long bookerId = em.persist(booker).getId();
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        long bookingInPastId = em.persist(bookingInPast).getId();
        long bookingCurrentId = em.persist(bookingCurrent).getId();
        em.persist(bookingInFuture);
        em.flush();

        List<Booking> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByEndDesc(bookerId, future2, Pageable.unpaged());
        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(bookingCurrentId);
        assertThat(bookings.get(1).getId()).isEqualTo(bookingInPastId);
    }

    @Test
    public void shouldFindNoBookingsByEndBeforeOrdered() {
        User booker = new User(0, "booker", "mail1@email.com");
        Item item1 = new Item(0, "item1", "desc1", true, booker, null);
        Item item2 = new Item(0, "item2", "desc2", true, booker, null);
        Item item3 = new Item(0, "item3", "desc3", true, booker, null);

        Booking bookingInPast = new Booking(0, past1, past2, item1, booker, BookingStatus.APPROVED);
        Booking bookingCurrent = new Booking(0, past2, future1, item2, booker, BookingStatus.APPROVED);
        Booking bookingInFuture = new Booking(0, future1, future2, item3, booker, BookingStatus.APPROVED);

        long bookerId = em.persist(booker).getId();
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.persist(bookingInPast);
        em.persist(bookingCurrent);
        em.persist(bookingInFuture);
        em.flush();

        LocalDateTime dateTime = LocalDateTime.of(1990, 1, 1, 1, 1);

        List<Booking> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByEndDesc(bookerId, dateTime, Pageable.unpaged());
        assertThat(bookings).hasSize(0);
    }

    @Test
    public void shouldFind2BookingsByStartAfterOrdered() {
        User booker = new User(0, "booker", "mail1@email.com");
        Item item1 = new Item(0, "item1", "desc1", true, booker, null);
        Item item2 = new Item(0, "item2", "desc2", true, booker, null);
        Item item3 = new Item(0, "item3", "desc3", true, booker, null);

        Booking bookingInPast = new Booking(0, past1, past2, item1, booker, BookingStatus.APPROVED);
        Booking bookingCurrent = new Booking(0, past2, future1, item2, booker, BookingStatus.APPROVED);
        Booking bookingInFuture = new Booking(0, future1, future2, item3, booker, BookingStatus.APPROVED);

        long bookerId = em.persist(booker).getId();
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        long bookingInPastId = em.persist(bookingInPast).getId();
        long bookingCurrentId = em.persist(bookingCurrent).getId();
        long bookingInFutureId = em.persist(bookingInFuture).getId();
        em.flush();

        List<Booking> bookings = bookingRepository.findByBookerIdAndStartAfterOrderByEndDesc(bookerId, past1, Pageable.unpaged());
        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(bookingInFutureId);
        assertThat(bookings.get(1).getId()).isEqualTo(bookingCurrentId);
    }

    @Test
    public void shouldFindNextBooking() {
        User booker = new User(0, "booker", "mail1@email.com");
        Item itemWith2BookingsAfter = new Item(0, "item1", "desc1", true, booker, null);

        Booking actualNextBooking = new Booking(0, future1, future2, itemWith2BookingsAfter, booker, BookingStatus.APPROVED);
        Booking nextAfterNextBooking = new Booking(0, future2, future2.plusDays(10), itemWith2BookingsAfter, booker, BookingStatus.APPROVED);

        em.persist(booker);
        long itemId = em.persist(itemWith2BookingsAfter).getId();
        long actualNextBookingId = em.persist(actualNextBooking).getId();
        actualNextBooking.setId(actualNextBookingId);
        em.persist(nextAfterNextBooking);
        em.flush();

        Booking nextBooking = bookingRepository.getNextBooking(itemId, LocalDateTime.now());
        assertThat(nextBooking).isEqualTo(actualNextBooking);
    }

    @Test
    public void shouldFindPreviousBooking() {
        User booker = new User(0, "booker", "mail1@email.com");
        Item itemWith2BookingsBefore = new Item(0, "item1", "desc1", true, booker, null);

        Booking previousBeforePreviousBooking = new Booking(0, past1.minusDays(10), past1, itemWith2BookingsBefore, booker, BookingStatus.APPROVED);
        Booking actualPreviousBooking = new Booking(0, past1, past2, itemWith2BookingsBefore, booker, BookingStatus.APPROVED);

        em.persist(booker);
        long itemId = em.persist(itemWith2BookingsBefore).getId();
        long actualPreviousBookingId = em.persist(actualPreviousBooking).getId();
        actualPreviousBooking.setId(actualPreviousBookingId);
        em.persist(previousBeforePreviousBooking);
        em.flush();

        Booking previousBooking = bookingRepository.getPreviousBooking(itemId, LocalDateTime.now());
        assertThat(previousBooking).isEqualTo(actualPreviousBooking);
    }
}
