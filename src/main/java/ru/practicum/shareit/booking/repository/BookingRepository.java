package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStatusOrderByEndDesc(long bookerId, BookingStatus bookingStatus, Pageable pageable);

    List<Booking> findByBookerIdAndEndBeforeOrderByEndDesc(long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfterOrderByEndDesc(long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdOrderByEndDesc(long bookerId, Pageable pageable);

    List<Booking> findByItemIn(Collection<Item> items);

    List<Booking> findByItemIdAndBookerIdAndStatusAndEndBefore(long itemId, long bookerId, BookingStatus status, LocalDateTime now);

    @Query(value = "select * from Bookings where item_id = :itemId and end_date < :now order by end_date desc limit 1",
            nativeQuery = true)
    Booking getPreviousBooking(long itemId, LocalDateTime now);

    @Query(value = "select * from Bookings where item_id = :itemId and start_date > :now order by start_date limit 1",
            nativeQuery = true)
    Booking getNextBooking(long itemId, LocalDateTime now);
}
