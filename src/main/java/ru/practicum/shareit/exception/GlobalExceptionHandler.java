package ru.practicum.shareit.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.practicum.shareit.exception.exceptions.*;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UnsupportedStatusException.class)
    @ResponseBody
    public ResponseEntity<ErrorMessage> handleUnsupportedStatusException(UnsupportedStatusException exc) {
        return new ResponseEntity<>(new ErrorMessage(exc.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalStateException.class, MethodArgumentNotValidException.class})
    public void handleBadRequests(Exception exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, exc.getMessage());
    }

    @ExceptionHandler(value = {NoSuchUserException.class, NoSuchItemException.class, NoSuchBookingException.class,
            MismatchedEntityRelationException.class})
    public void handleMissingEntityExceptions(RuntimeException exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, exc.getMessage());
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class, MissingRequestHeaderException.class})
    public void handleConstraintViolationException(Exception exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exc.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public void handleSQLException(SQLException exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_CONFLICT, exc.getMessage());
    }

    @ExceptionHandler(value = {ConflictingFieldsException.class, PersistenceException.class})
    public void handleConflictingFieldsException(RuntimeException exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_CONFLICT, exc.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public void handleSecurityException(SecurityException exc, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_FORBIDDEN, exc.getMessage());
    }
}
