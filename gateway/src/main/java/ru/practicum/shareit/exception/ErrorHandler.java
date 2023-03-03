package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.practicum.shareit.booking.validation.IncorrectBookingTimeException;

import javax.validation.ConstraintDeclarationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingRequestHeaderExceptionHandler(final MissingRequestHeaderException e) {
        return new ErrorResponse("Missing request header error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(final NotFoundException e) {
        return new ErrorResponse("Entity not found error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidExceptionHandler(final MethodArgumentNotValidException e) {
        return new ErrorResponse("Not valid method argument: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationExceptionHandle(final ConstraintViolationException e) {
        return new ErrorResponse("method constraint exception: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse illegalBookingStateExceptionHandler(final ConstraintDeclarationException e) {
        return new ErrorResponse("Unknown state: " + e.getMessage(), "");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse incorrectBookingTimeExceptionHandler(final IncorrectBookingTimeException e) {
        return new ErrorResponse("IncorrectBookingTime error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse httpClientErrorExceptionNotFoundHandler(final WebClientResponseException.NotFound e) {
        return new ErrorResponse("Handler not found: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpClientErrorExceptionBadRequestHandler(final WebClientResponseException.BadRequest e) {
        return new ErrorResponse("Wrong handler: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse httpClientErrorExceptionConflictHandler(final WebClientResponseException.Conflict e) {
        return new ErrorResponse("Handler conflict: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse internalErrorExceptionHandler(final InternalError e) {
        return new ErrorResponse("Unexpected error: ", e.getMessage());
    }
}