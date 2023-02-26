package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingTest {
    @Test
    public void twoBookingMustBeEquals() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(5);
        User user = new User(1L, "name", "e@email.com");
        Item item = new Item(1L, "name", "description", Boolean.TRUE, user, null);

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        Booking booking1 = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        assertEquals(booking, booking1);
        assertEquals(booking.hashCode(), booking1.hashCode());
    }
}