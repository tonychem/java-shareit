package ru.practicum.shareit.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.exception.exceptions.ConflictingFieldsException;
import ru.practicum.shareit.exception.exceptions.NoSuchItemException;
import ru.practicum.shareit.exception.exceptions.NoSuchUserException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public void handleIllegalStateException(IllegalStateException exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, exc.getMessage());
    }

    @ExceptionHandler(value = {NoSuchUserException.class, NoSuchItemException.class})
    public void handleMissingEntityExceptions(RuntimeException exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, exc.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public void handleMethodArgumentNotValidException(ConstraintViolationException exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, exc.getMessage());
    }

    @ExceptionHandler(ConflictingFieldsException.class)
    public void handleConflictingFieldsException(ConflictingFieldsException exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_CONFLICT, exc.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public void handleSecurityException(SecurityException exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_FORBIDDEN, exc.getMessage());
    }
}
