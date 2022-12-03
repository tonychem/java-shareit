package ru.practicum.shareit.MVCTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;
import ru.practicum.shareit.requests.ItemRequestController;
import ru.practicum.shareit.requests.dto.IncomingItemRequestDto;
import ru.practicum.shareit.requests.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private final IncomingItemRequestDto validIncomingRequest = new IncomingItemRequestDto("screwdriver");
    private final IncomingItemRequestDto invalidIncomingRequest = new IncomingItemRequestDto("");
    private final OutgoingItemRequestDto savedValidRequest =
            new OutgoingItemRequestDto(1, "screwdriver", LocalDateTime.now(), Collections.emptyList());

    @Test
    public void shouldSaveValidRequest() throws Exception {
        when(itemRequestService.createRequest(1, validIncomingRequest))
                .thenReturn(savedValidRequest);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(validIncomingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @Test
    public void shouldReturn400WhenInvalidIncomingRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(invalidIncomingRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturn404WhenUserNotFound() throws Exception {
        when(itemRequestService.createRequest(100, validIncomingRequest))
                .thenThrow(NoSuchUserException.class);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 100)
                        .content(mapper.writeValueAsString(invalidIncomingRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnListOfRequestsByOthersUnpaginated() throws Exception {
        when(itemRequestService.getListOfRequestsByOthers(1, null, null))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(invalidIncomingRequest)))
                .andExpect(content().string("[]"));
    }

    @Test
    public void shouldReturnListOfRequestsByOthersUnpaginatedWhenOneParamIsMissing() throws Exception {
        when(itemRequestService.getListOfRequestsByOthers(1, 1, null))
                .thenReturn(Collections.emptyList());

        when(itemRequestService.getListOfRequestsByOthers(1, null, 1))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .content(mapper.writeValueAsString(invalidIncomingRequest)))
                .andExpect(content().string("[]"));

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("size", "1")
                        .content(mapper.writeValueAsString(invalidIncomingRequest)))
                .andExpect(content().string("[]"));
    }

    @Test
    public void shouldReturnItemRequestById() throws Exception {
        when(itemRequestService.requestById(anyLong(), anyLong()))
                .thenReturn(savedValidRequest);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedValidRequest.getId()), Long.class));
    }
}
