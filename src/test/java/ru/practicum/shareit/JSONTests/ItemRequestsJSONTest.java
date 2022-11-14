package ru.practicum.shareit.JSONTests;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.IncomingItemRequestDto;
import ru.practicum.shareit.requests.dto.OutgoingItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestsJSONTest {
    @Autowired
    private JacksonTester<IncomingItemRequestDto> incomingItemRequestDtoTester;

    @Autowired
    private JacksonTester<OutgoingItemRequestDto> outgoingItemRequestDtoTester;

    @SneakyThrows
    @Test
    public void incomingItemRequestDtoRegularTest() {
        IncomingItemRequestDto dto = new IncomingItemRequestDto("description");
        JsonContent<IncomingItemRequestDto> content = incomingItemRequestDtoTester.write(dto);

        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @SneakyThrows
    @Test
    public void outgoingItemRequestDtoRegularTest() {
        ItemDto itemDto = new ItemDto(5, null, null, null, null, null);
        List<ItemDto> list = List.of(itemDto);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter excludingMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        OutgoingItemRequestDto dto = new OutgoingItemRequestDto(1L, "description", now, list);

        JsonContent<OutgoingItemRequestDto> content = outgoingItemRequestDtoTester.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).extractingJsonPathStringValue("$.created").contains(now.format(excludingMillis));
        assertThat(content).extractingJsonPathArrayValue("$.items").hasSize(1).hasToString("[{id=5}]");
    }

    @SneakyThrows
    @Test
    public void outgoingItemRequestDtoNullValuesTest() {
        OutgoingItemRequestDto dto = new OutgoingItemRequestDto(1L, "description", null, null);

        JsonContent<OutgoingItemRequestDto> content = outgoingItemRequestDtoTester.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).doesNotHaveJsonPath("$.created");
        assertThat(content).doesNotHaveJsonPath("$.items");
    }
}
