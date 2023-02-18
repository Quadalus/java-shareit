package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.MyPageRequest;

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
    private final BookingService bookingService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto addBooking(@RequestHeader(USER_HEADER) Long userId,
                                 @Valid @RequestBody BookingDtoFromRequest bookingDtoFromRequest) {
        return bookingService.addBooking(userId, bookingDtoFromRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingConfirmation(@RequestHeader(USER_HEADER) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingService.bookingConfirmation(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_HEADER) Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestHeader(USER_HEADER) Long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return bookingService.getBookingsByBooker(MyPageRequest.of(from, size), userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(USER_HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state,
                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return bookingService.getBookingsByOwner(MyPageRequest.of(from, size), userId, state);
    }
}
