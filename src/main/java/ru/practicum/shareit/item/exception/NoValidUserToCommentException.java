package ru.practicum.shareit.item.exception;

public class NoValidUserToCommentException extends RuntimeException {
    public NoValidUserToCommentException(String message) {
        super(message);
    }
}
