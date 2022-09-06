package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_idAndStatus(long bookerId, BookingStatus bookingStatus, Sort sort);

    List<Booking> findByBooker_idAndEndBefore(long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_idAndStartAfter(long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBooker_idAndStartBeforeAndEndAfter(long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_id(long bookerId, Sort sort);

    List<Booking> findByItemIn(Collection<Item> items);

    List<Booking> findByItem_idAndBooker_idAndStatusAndEndBefore(long itemId, long bookerId, BookingStatus status, LocalDateTime now);

    @Query(value = "select * from Bookings where item_id = :itemId and end_date < :now order by end_date desc limit 1",
    nativeQuery = true)
    Booking getPreviousBooking(long itemId, LocalDateTime now);

    @Query(value = "select * from Bookings where item_id = :itemId and start_date > :now order by start_date limit 1",
            nativeQuery = true)
    Booking getNextBooking(long itemId, LocalDateTime now);
}
