package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptions.MismatchedEntityRelationException;
import ru.practicum.shareit.exception.exceptions.NoSuchBookingException;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.exception.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingsServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;


    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("Не существует пользователя с id = " + userId));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NoSuchUserException("Не существует вещи с id = " + bookingDto.getItemId()));

        if (userId == item.getOwner().getId()) {
            throw new MismatchedEntityRelationException("Владелец вещи не может забронировать свою");
        }

        if (!item.getAvailable()) {
            throw new IllegalStateException("Уже существует бронирование на вещь с id = " + item.getId());
        }

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalStateException("Создание бронирования с датой конца раньше даты начала");
        }

        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto setBookingStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchBookingException("Не существует бронирования с id = " + bookingId));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new MismatchedEntityRelationException("Не владелец вещи!");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new IllegalStateException("Уже одобрено");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking newBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(newBooking);
    }

    @Override
    @Transactional
    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchBookingException("Не существует бронирования с id = " + bookingId));

        if (userId == booking.getBooker().getId() ||
                userId == booking.getItem().getOwner().getId()) {
            return bookingMapper.toBookingDto(booking);
        } else {
            throw new MismatchedEntityRelationException("Не владелец вещи или арендатор!");
        }
    }

    @Override
    @Transactional
    public List<BookingDto> getListOfBookingsByState(long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + userId);
        }

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "end");
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ("CURRENT"):
                bookings = bookingRepository.findByBooker_idAndStartBeforeAndEndAfter(userId, now, now, sort);
                break;
            case ("PAST"):
                bookings = bookingRepository.findByBooker_idAndEndBefore(userId, now, sort);
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findByBooker_idAndStartAfter(userId, now, sort);
                break;
            case ("WAITING"):
                bookings = bookingRepository.findByBooker_idAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findByBooker_idAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            case ("ALL"):
                bookings = bookingRepository.findByBooker_id(userId, sort);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional
    public List<BookingDto> getListOfBookedItemsByOwner(long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + userId);
        }

        List<Booking> allBookedItemsOfUser = bookingRepository.findByItemIn(itemRepository.findItemsOwnedBy(userId));
        LocalDateTime now = LocalDateTime.now();

        Comparator<Booking> compareByEndTimeDesc = (x, y) -> {
            if (x.getEnd().isAfter(y.getEnd())) {
                return -1;
            } else if (x.getEnd().isBefore(y.getEnd())) {
                return 1;
            } else return 0;
        };


        List<BookingDto> result;

        switch (state) {
            case ("CURRENT"):
                result = allBookedItemsOfUser.stream()
                        .filter(x -> x.getStart().isAfter(now) && x.getEnd().isBefore(now))
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            case ("PAST"):
                result = allBookedItemsOfUser.stream()
                        .filter(x -> x.getStart().isBefore(now) && x.getEnd().isAfter(now))
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            case ("FUTURE"):
                result = allBookedItemsOfUser.stream()
                        .filter(x -> x.getStart().isAfter(now))
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            case ("WAITING"):
                result = allBookedItemsOfUser.stream()
                        .filter(x -> x.getStatus() == BookingStatus.WAITING)
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            case ("REJECTED"):
                result = allBookedItemsOfUser.stream()
                        .filter(x -> x.getStatus() == BookingStatus.REJECTED)
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            case ("ALL"):
                result = allBookedItemsOfUser.stream()
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return result;
    }
}
