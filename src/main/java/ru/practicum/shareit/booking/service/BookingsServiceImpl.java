package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptions.*;
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
                .orElseThrow(() -> new NoSuchItemException("Не существует вещи с id = " + bookingDto.getItemId()));

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
    public List<BookingDto> getListOfBookingsByState(long userId, String state, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + userId);
        }

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable;

        if (from == null & size == null) {
            pageable = Pageable.unpaged();
        } else if (from < 0 || size < 1) {
            throw new IllegalStateException("Неверные параметры запроса");
        } else {
            pageable = PageRequest.of(from / size, size);
        }

        switch (state) {
            case ("CURRENT"):
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(userId, now, now, pageable);
                break;
            case ("PAST"):
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByEndDesc(userId, now, pageable);
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByEndDesc(userId, now, pageable);
                break;
            case ("WAITING"):
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(userId, BookingStatus.WAITING, pageable);
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(userId, BookingStatus.REJECTED, pageable);
                break;
            case ("ALL"):
                bookings = bookingRepository.findByBookerIdOrderByEndDesc(userId, pageable);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<BookingDto> getListOfBookedItemsByOwner(long userId, String state, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUserException("Не существует пользователя с id = " + userId);
        }

        List<Booking> allBookedItemsOfUser = bookingRepository.findByItemIn(itemRepository.findItemsOwnedBy(userId));
        LocalDateTime now = LocalDateTime.now();

        Comparator<Booking> compareByEndTimeDesc = (bookingFirst, bookingSecond) -> {
            if (bookingFirst.getEnd().isAfter(bookingSecond.getEnd())) {
                return -1;
            } else if (bookingFirst.getEnd().isBefore(bookingSecond.getEnd())) {
                return 1;
            } else return 0;
        };


        List<BookingDto> fullListOfBookings;

        switch (state) {
            case ("CURRENT"):
                fullListOfBookings = allBookedItemsOfUser.stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            case ("PAST"):
                fullListOfBookings = allBookedItemsOfUser.stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isBefore(now))
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            case ("FUTURE"):
                fullListOfBookings = allBookedItemsOfUser.stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            case ("WAITING"):
                fullListOfBookings = allBookedItemsOfUser.stream()
                        .filter(booking -> booking.getStatus() == BookingStatus.WAITING)
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            case ("REJECTED"):
                fullListOfBookings = allBookedItemsOfUser.stream()
                        .filter(booking -> booking.getStatus() == BookingStatus.REJECTED)
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            case ("ALL"):
                fullListOfBookings = allBookedItemsOfUser.stream()
                        .sorted(compareByEndTimeDesc)
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toUnmodifiableList());
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        if (from == null & size == null) {
            return fullListOfBookings;
        } else if (from < 1 || size < 1) {
            throw new IllegalStateException("Неверные параметры запроса");
        } else {
            return fullListOfBookings.stream()
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toUnmodifiableList());
        }
    }
}
