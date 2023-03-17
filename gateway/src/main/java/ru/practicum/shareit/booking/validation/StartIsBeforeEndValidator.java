package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.validation.annotation.StartIsBeforeEnd;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartIsBeforeEndValidator
        implements ConstraintValidator<StartIsBeforeEnd, BookingDtoFromRequest> {

    @Override
    public boolean isValid(BookingDtoFromRequest bookingDto, ConstraintValidatorContext context) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        if (start == null || end == null) {
            throw new IncorrectBookingTimeException(
                    String.format("end time and start must be not null: end %s >---< start %s", end, start));
        }

        if (start.isAfter(end)) {
            throw new IncorrectBookingTimeException(
                    String.format("end time must be after the start: end %s >---< start %s", end, start)
            );
        }
        return start.isBefore(end);
    }
}
