package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingDtoFromRequest bookingDtoFromRequest);

    BookingDto bookingConfirmation(Long userId, Long bookingId, Boolean isApproved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsByBooker(Pageable pageable, Long userId, String state);

    List<BookingDto> getBookingsByOwner(Pageable pageable, Long userId, String state);
}
