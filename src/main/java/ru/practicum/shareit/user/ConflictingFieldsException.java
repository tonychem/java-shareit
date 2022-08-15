package ru.practicum.shareit.user;

public class ConflictingFieldsException extends RuntimeException {
    public ConflictingFieldsException(String message) {
        super(message);
    }
}
