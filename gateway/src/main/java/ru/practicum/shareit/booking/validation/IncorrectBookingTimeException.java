package ru.practicum.shareit.booking.validation;

public class IncorrectBookingTimeException extends RuntimeException {
    public IncorrectBookingTimeException(String message) {
        super(message);
    }
}
