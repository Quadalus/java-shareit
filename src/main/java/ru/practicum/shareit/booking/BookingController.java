package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;


    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody BookingDtoFromRequest bookingDtoFromRequest) {
        return bookingService.addBooking(userId, bookingDtoFromRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingConfirmation(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        return bookingService.bookingConfirmation(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}
