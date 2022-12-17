package ru.practicum.shareit.MVCTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Captor
    private ArgumentCaptor<String> argumentCaptor;

    private final BookingDto invalidBookingDto = new BookingDto(0, LocalDateTime.of(1995, 6, 30, 1, 1, 1),
            LocalDateTime.now(), null, null, null, 0, 0);

    @Test
    public void shouldReturnListOfBookingsWithoutRequestParams() throws Exception {
        when(bookingService.getListOfBookingsByState(1, null, null, null))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
        verify(bookingService).getListOfBookingsByState(eq(1L), argumentCaptor.capture(), isNull(), isNull());
        assertThat(argumentCaptor.getValue()).isEqualTo("ALL");
    }

    @Test
    public void shouldReturnListOfOwnBookingsWithoutRequestParams() throws Exception {
        when(bookingService.getListOfBookedItemsByOwner(1, null, null, null))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
        verify(bookingService).getListOfBookedItemsByOwner(eq(1L), argumentCaptor.capture(), isNull(), isNull());
        assertThat(argumentCaptor.getValue()).isEqualTo("ALL");
    }

    @Disabled("Тест не имеет смысла после переноса валидации в gateway")
    @Test
    public void shouldThrow400WhenCreatingInvalidBooking() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(invalidBookingDto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldThrow400WhenPatchingWithoutApproved() throws Exception {
        mockMvc.perform(patch("/bookings/{bookindId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(invalidBookingDto)))
                .andExpect(status().is4xxClientError());
    }
}
