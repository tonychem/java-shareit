package ru.practicum.shareit.MVCTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.IncomingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutcomingCommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    private final ItemDto validItem =
            new ItemDto(0, "driver", "screwDriver", null, null, null);
    private final ItemDto invalidItem =
            new ItemDto(0, "", "", null, null, null);

    private final ItemDto savedValidItem =
            new ItemDto(1, "driver", "screwDriver", null, null, null);

    private final IncomingCommentDto validIncomingComment =
            new IncomingCommentDto(0, "someDescription");

    @Test
    public void shouldSaveValidItem() throws Exception {
        when(itemService.create(1, validItem)).thenReturn(savedValidItem);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(validItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedValidItem.getId()), Long.class));
    }

    @Test
    public void shouldReturn500WithoutHeader() throws Exception {
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(validItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void shouldReturn400WhenSavingInvalidItem() throws Exception {
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(invalidItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnEmptyListOnEmptySearchParam() throws Exception {
        when(itemService.searchByNameAndDescription("")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/search")
                        .param("text", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(""));
    }

    @Test
    public void shouldCreateValidComment() throws Exception {
        when(itemService.createComment(1, 1, validIncomingComment))
                .thenReturn(new OutcomingCommentDto(1, "someDescription", "author", LocalDateTime.now()));

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(validIncomingComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.created", notNullValue()));
    }

    @Test
    public void shouldGetExistingItem() throws Exception {
        when(itemService.itemById(1, 1))
                .thenReturn(new ItemBookingCommentDataDto(1, "name", "desc", null,
                        null, null, null, null));

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void shouldReturn404WhenUserNotFoundOnPatching() throws Exception {
        when(itemService.update(100, 1, validItem))
                .thenThrow(NoSuchUserException.class);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "100")
                        .content(mapper.writeValueAsString(validItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}
