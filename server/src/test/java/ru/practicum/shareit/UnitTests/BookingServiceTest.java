package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingsServiceImpl;
import ru.practicum.shareit.exception.exceptions.MismatchedEntityRelationException;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.exception.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        assertEquals(bookingService.getListOfBookingsByState(1, "WAITING", 1, 1).get(0).getStatus(),
                BookingStatus.WAITING);

        assertEquals(bookingService.getListOfBookingsByState(1, "ALL", 1, 1).get(0).getStatus(),
                BookingStatus.APPROVED);
    }

    @Test
    public void shouldThrowExceptionsWhenCreatingBookingWithConstraints() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(1, "user", "email")));

        when(itemRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            long number = invocationOnMock.getArgument(0, Long.class);
            //при item_id == 1, пользователь бронирует свою вещь
            if (number == 1) return Optional.of(new Item(1, "name", "description", true,
                    new User(1, "name", "email"), null));
                //при item_id == 2 предмет недоступен для бронирования
            else if (number == 2) return Optional.of(new Item(2, "name2", "description2", false,
                    new User(2, "name2", "email2"), null));
            else return Optional.of(new Item(3, "name3", "description3", true,
                        new User(3, "name3", "email3"), null));
        });

        //Пользователь бронирует собственную вещь
        BookingDto fromOwner = new BookingDto(0, null, null, null, null,
                null, 1, 1);

        assertThrows(MismatchedEntityRelationException.class, () -> {
            bookingService.createBooking(1, fromOwner);
        });

        //Бронирование занятной вещи
        BookingDto bookedItem = new BookingDto(0, null, null, null, null, null, 2,
                3);
        assertThrows(IllegalStateException.class, () -> {
            bookingService.createBooking(3, bookedItem);
        });

        //Неверные дата начала и конца бронирования
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(10);
        BookingDto wrongDates = new BookingDto(0, start, end, null, null, null, 0, 0);
        assertThrows(IllegalStateException.class, () -> {
            bookingService.createBooking(2, wrongDates);
        });
    }

    @Test
    public void shouldThrowExceptionWhenStatusIsSetByAnother() {
        when(bookingRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            //Если передается 1 -> статус APPROVED, 2 -> статус WAITING
            long number = invocationOnMock.getArgument(0, Long.class);

            User owner = new User(1, "owner", "email");
            User booker = new User(2, "booker", "email2");

            Item item = new Item(1, "item", "description", true, owner, null);
            if (number == 1) return Optional.of(new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                    item, booker, BookingStatus.APPROVED));
            else return Optional.of(new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                    item, booker, BookingStatus.WAITING));
        });

        assertThrows(MismatchedEntityRelationException.class, () -> {
            bookingService.setBookingStatus(2, 1, true);
        });
    }
}
