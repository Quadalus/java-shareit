package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingDtoMapperTest {
    private final User user = new User(1L, "name", "abc@email.com");
    private final Item item = new Item(1L, "name", "description", Boolean.TRUE, user, null);
    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now().plusDays(5);

    @Test
    void toBookingDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto bookingDto = BookingDtoMapper.toBookingDto(booking);

        assertNotNull(booking);
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertNotNull(bookingDto.getItem());
        assertNotNull(bookingDto.getBooker());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void toBookingShortDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(start);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        BookingShortDto bookingShortDto = BookingDtoMapper.toBookingShortDto(booking);

        assertNotNull(booking);
        assertEquals(booking.getId(), bookingShortDto.getId());
        assertEquals(booking.getEnd(),  bookingShortDto.getEnd());
        assertEquals(booking.getStart(),  bookingShortDto.getStart());
        assertEquals(booking.getBooker().getId(), bookingShortDto.getBookerId());
    }

    @Test
    void fromBookingDto() {
        BookingDtoFromRequest bookingDtoFromRequest = new BookingDtoFromRequest(1L, start, end);

        Booking booking = BookingDtoMapper.fromBookingDto(bookingDtoFromRequest, user, item);

        assertNotNull(booking);
        assertEquals(booking.getStart(), start);
        assertEquals(booking.getEnd(), end);
        assertEquals(booking.getItem(), item);
        assertEquals(booking.getBooker(), user);
        assertEquals(booking.getStatus(), BookingStatus.WAITING);
    }
}