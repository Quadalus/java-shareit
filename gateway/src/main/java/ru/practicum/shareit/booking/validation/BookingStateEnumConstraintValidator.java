package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.validation.annotation.StateEnum;

import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingStateEnumConstraintValidator
        implements ConstraintValidator<StateEnum, String> {

    @Override
    public boolean isValid(String possibleState, ConstraintValidatorContext context) {
        try {
            State.valueOf(possibleState);
            return true;
        } catch (IllegalArgumentException ex) {
            throw new IncorrectStateException(possibleState);
        }
    }

    private static class IncorrectStateException extends ConstraintDeclarationException {
        public IncorrectStateException(String message) {
            super(message);
        }
    }
}