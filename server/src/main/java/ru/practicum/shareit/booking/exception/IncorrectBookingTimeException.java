package ru.practicum.shareit.booking.exception;

public class IncorrectBookingTimeException extends RuntimeException {
    public IncorrectBookingTimeException(String message) {
        super(message);
    }
}
