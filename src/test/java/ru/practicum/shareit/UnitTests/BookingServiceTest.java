package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingsServiceImpl;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.exception.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    private BookingService bookingService;

    @BeforeEach
    public void init() {
        bookingService = new BookingsServiceImpl(userRepository, itemRepository, bookingRepository, new BookingMapper());
    }

    @Test
    public void shouldReturnListOfBookingsByStateRegularTest() {
        when(userRepository.existsById(anyLong())).thenAnswer(invocationOnMock -> {
            long number = invocationOnMock.getArgument(0, Long.class);
            return number > 0;
        });

        when(bookingRepository.findByBookerIdAndStatusOrderByEndDesc(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(new Booking(0, LocalDateTime.now(), LocalDateTime.now(), new Item(1, null, null, null, null, null),
                        new User(1, null, null), BookingStatus.WAITING)));

        when(bookingRepository.findByBookerIdOrderByEndDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(new Booking(0, LocalDateTime.now(), LocalDateTime.now(), new Item(1, null, null, null, null, null),
                        new User(1, null, null), BookingStatus.APPROVED)));

        assertThrows(NoSuchUserException.class, () -> {
            bookingService.getListOfBookingsByState(-1, "ALL", 1, 1);
        });

        assertThrows(UnsupportedStatusException.class, () -> {
            bookingService.getListOfBookingsByState(1, "somestatus", 1, 1);
        });

        assertThrows(IllegalStateException.class, () -> {
            bookingService.getListOfBookingsByState(1, "ALL", 0, 0);
        });

        assertEquals(bookingService.getListOfBookingsByState(1, "WAITING", 1, 1).get(0).getStatus(),
                BookingStatus.WAITING);

        assertEquals(bookingService.getListOfBookingsByState(1, "ALL", 1, 1).get(0).getStatus(),
                BookingStatus.APPROVED);
    }

}
