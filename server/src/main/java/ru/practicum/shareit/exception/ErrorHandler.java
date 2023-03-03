package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.item.exception.NoValidUserToCommentException;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;

@RestControllerAdvice
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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse incorrectParameterExceptionHandler(final IncorrectParameterException e) {
        return new ErrorResponse("Incorrect parameter error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse internalErrorExceptionHandler(final InternalError e) {
        return new ErrorResponse("Unexpected error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse userNotHaveAccessToCommentExceptionHandler(final NoValidUserToCommentException e) {
        return new ErrorResponse("NoValidUserToComment error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse incorrectBookingTimeExceptionHandler(final IncorrectBookingTimeException e) {
        return new ErrorResponse("IncorrectBookingTime error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse itemNotAvailableExceptionHandler(final ItemNotAvailableException e) {
        return new ErrorResponse("ItemNotAvailable error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse illegalBookingStateExceptionHandler(final IllegalBookingStateException e) {
        return new ErrorResponse("Unknown state: " + e.getMessage(), "");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(final BookingNotFoundException e) {
        return new ErrorResponse("Entity not found error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse bookingAlreadyApprovedExceptionHandler(final BookingAlreadyApprovedException e) {
        return new ErrorResponse("BookingAlreadyApproved error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userAlreadyItemOwnerExceptionHandler(final UserAlreadyItemOwnerException e) {
        return new ErrorResponse("Entity not found error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(final ItemRequestNotFoundException e) {
        return new ErrorResponse("Entity not found error: ", e.getMessage());
    }
}