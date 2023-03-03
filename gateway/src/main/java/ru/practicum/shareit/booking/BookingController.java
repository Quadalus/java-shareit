package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.validation.annotation.StateEnum;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBooking(@RequestHeader(USER_HEADER) Long userId,
                                 @Valid @RequestBody BookingDtoFromRequest bookingDtoFromRequest) {
        return bookingClient.addBooking(userId, bookingDtoFromRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{bookingId}")
    public BookingDto bookingConfirmation(@RequestHeader(USER_HEADER) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingClient.bookingConfirmation(userId, bookingId, approved);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_HEADER) Long userId,
                                     @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestHeader(USER_HEADER) Long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) @StateEnum String state,
                                                @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(required = false, defaultValue = "20") @Positive int size) {
        return bookingClient.getBookingsByBooker(from, size, userId, state);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(USER_HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL", required = false) @StateEnum String state,
                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(required = false, defaultValue = "20") @Positive int size) {
        return bookingClient.getBookingsByOwner(from, size, userId, state);
    }
}
