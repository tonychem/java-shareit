package ru.practicum.shareit.exception.exceptions;

public class NoSuchBookingException extends RuntimeException {
    public NoSuchBookingException(String message) {
        super(message);
    }
}
