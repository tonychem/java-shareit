package ru.practicum.shareit.JSONTests;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutcomingCommentDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemsJSONTest {

    @Autowired
    private JacksonTester<ItemDto> itemDtoTester;

    @Autowired
    private JacksonTester<IncomingCommentDto> incomingCommentDtoTester;

    @Autowired
    private JacksonTester<ItemBookingCommentDataDto> itemBookingCommentDataDtoTester;

    @Autowired
    private JacksonTester<OutcomingCommentDto> outcomingCommentDtoTester;

    @SneakyThrows
    @Test
    public void itemDtoTestRegular() {
        User user = new User(1, "user", "user@email.com");
        ItemDto dto = new ItemDto(10, "item", "description", true, user, 15l);

        JsonContent<ItemDto> content = itemDtoTester.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(15);
        assertThat(content).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).extractingJsonPathValue("$.owner").hasFieldOrPropertyWithValue("name", "user")
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "user@email.com");
    }

    @SneakyThrows
    @Test
    public void incomingCommentDtoRegularTest() {
        IncomingCommentDto dto = new IncomingCommentDto(1, "comment");

        JsonContent<IncomingCommentDto> content = incomingCommentDtoTester.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("comment");
    }

    @SneakyThrows
    @Test
    public void incomingBookingCommentDtoRegularTest() {
        User user = new User(1, "user", "user@email.com");
        BookingDtoShort previous = new BookingDtoShort(1l, 10l);
        BookingDtoShort next = new BookingDtoShort(3l, 11l);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);

        OutcomingCommentDto comment1 = new OutcomingCommentDto(1l, "comment1", "author", now);
        OutcomingCommentDto comment2 = new OutcomingCommentDto(2l, "comment2", "author", tenDaysAgo);

        List<OutcomingCommentDto> list = List.of(comment1, comment2);

        ItemBookingCommentDataDto dto = new ItemBookingCommentDataDto(10, "item", "description",
                true, user, previous, next, list);

        JsonContent<ItemBookingCommentDataDto> content = itemBookingCommentDataDtoTester.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(content).extractingJsonPathValue("$.owner").hasFieldOrPropertyWithValue("name", "user")
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "user@email.com");
        assertThat(content).extractingJsonPathValue("$.lastBooking").hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("bookerId", 10);
        assertThat(content).extractingJsonPathValue("$.nextBooking").hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("bookerId", 11);

        assertThat(content).extractingJsonPathArrayValue("$.comments").hasSize(2);
    }

    @SneakyThrows
    @Test
    public void outcomingCommentDtoRegularTest() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter excludingMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        OutcomingCommentDto dto = new OutcomingCommentDto(1, "comment", "author", now);

        JsonContent<OutcomingCommentDto> content = outcomingCommentDtoTester.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(content).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
        assertThat(content).extractingJsonPathStringValue("$.created").contains(now.format(excludingMillis));
    }
}
