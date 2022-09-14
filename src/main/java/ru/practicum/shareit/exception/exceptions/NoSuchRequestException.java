package ru.practicum.shareit.exception.exceptions;

public class NoSuchRequestException extends RuntimeException {
    public NoSuchRequestException(String message) {
        super(message);
    }
}
