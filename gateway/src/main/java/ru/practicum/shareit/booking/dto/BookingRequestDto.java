package ru.practicum.shareit.booking.dto;

import lombok.Value;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Value
public class BookingRequestDto {
    long itemId;
    @FutureOrPresent
    LocalDateTime start;
    @Future
    LocalDateTime end;
}
