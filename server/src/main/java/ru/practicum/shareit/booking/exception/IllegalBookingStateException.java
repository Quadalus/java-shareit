package ru.practicum.shareit.booking.exception;

public class IllegalBookingStateException extends RuntimeException {
    public IllegalBookingStateException(String message) {
        super(message);
    }
}
