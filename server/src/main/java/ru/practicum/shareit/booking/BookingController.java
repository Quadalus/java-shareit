package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.MyPageRequest;

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
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBooking(@RequestHeader(USER_HEADER) Long userId,
                                 @RequestBody BookingDtoFromRequest bookingDtoFromRequest) {
        return bookingService.addBooking(userId, bookingDtoFromRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{bookingId}")
    public BookingDto bookingConfirmation(@RequestHeader(USER_HEADER) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingService.bookingConfirmation(userId, bookingId, approved);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_HEADER) Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestHeader(USER_HEADER) Long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "20") int size) {
        return bookingService.getBookingsByBooker(MyPageRequest.of(from, size), userId, state);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(USER_HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "20") int size) {
        return bookingService.getBookingsByOwner(MyPageRequest.of(from, size), userId, state);
    }
}
