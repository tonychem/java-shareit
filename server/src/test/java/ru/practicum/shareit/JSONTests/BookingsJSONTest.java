package ru.practicum.shareit.JSONTests;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingsJSONTest {

    @Autowired
    private JacksonTester<BookingDto> bookingDtoJacksonTester;

    @Autowired
    private JacksonTester<BookingDtoShort> bookingDtoShortJacksonTester;

    @SneakyThrows
    @Test
    public void bookingDtoRegularTest() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        Item item = new Item();
        User booker = new User();
        BookingStatus status = BookingStatus.WAITING;
        DateTimeFormatter excludingMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        BookingDto bookingDto = new BookingDto(1, start, end, item, booker, status, 0, 0);
        JsonContent<BookingDto> content = bookingDtoJacksonTester.write(bookingDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.itemId").isEqualTo(0);
        assertThat(content).extractingJsonPathNumberValue("$.bookerId").isEqualTo(0);

        assertThat(content).extractingJsonPathStringValue("$.start").contains(start.format(excludingMillis));
        assertThat(content).extractingJsonPathStringValue("$.end").contains(end.format(excludingMillis));
    }

    @SneakyThrows
    @Test
    public void bookingDtoNullFieldsTest() {
        BookingDto bookingDto = new BookingDto(1, null, null, null, null, null, 2, 3);

        JsonContent<BookingDto> content = bookingDtoJacksonTester.write(bookingDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(content).extractingJsonPathNumberValue("$.bookerId").isEqualTo(3);

        assertThat(content).doesNotHaveJsonPath("$.start");
        assertThat(content).doesNotHaveJsonPath("$.end");
        assertThat(content).doesNotHaveJsonPath("$.item");
        assertThat(content).doesNotHaveJsonPath("$.booker");
        assertThat(content).doesNotHaveJsonPath("$.status");
    }

    @SneakyThrows
    @Test
    public void bookingDtoShortRegularTest() {
        BookingDtoShort dto = new BookingDtoShort(1L, 2L);

        JsonContent<BookingDtoShort> content = bookingDtoShortJacksonTester.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
    }
}
