package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    Long itemId = 1L;
    LocalDateTime time = LocalDateTime.now();
    User owner = new User(2L, "ownerName", "owner@email.com");
    User booker = new User(1L, "bookerName", "booker@email.com");
    Item item = new Item(itemId, "name", "description", Boolean.TRUE, owner, null);

    @AfterEach
    public void tearDown() {
        bookingRepository.deleteAll();
    }

    @Test
    void findBookingByBookerIdOrderByStartDescTest() {
        Booking booking = new Booking(1L, time.plusDays(5), time.plusDays(15), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long bookerId1 = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingByBookerIdOrderByStartDesc(MyPageRequest.of(0, 5), bookerId1);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));

    }

    @Test
    void findBookingByBookerIdAndStartIsBeforeAndEndIsAfterTest() {
        Booking booking1 = new Booking(2L, time.minusDays(5), time.plusDays(15), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking1);
        Long bookerId1 = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(MyPageRequest.of(0, 20), bookerId1, time, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByBookerIdAndEndIsBeforeTest() {
        Booking booking2 = new Booking(3L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking2);
        Long bookerId1 = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByBookerIdAndEndIsBefore(MyPageRequest.of(0, 20), bookerId1, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByBookerIdAndStartIsAfterOrderByStartDescTest() {
        Booking booking3 = new Booking(4L, time.plusDays(5), time.plusDays(12), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking3);
        Long bookerId1 = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByBookerIdAndStartIsAfterOrderByStartDesc(MyPageRequest.of(0, 20), bookerId1, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByBookerIdAndStatusEqualsWhenStatusIsWaitingAndThenReturnedListOfWaitingBookingTest() {
        Booking booking4 = new Booking(5L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking4);
        Long bookerId1 = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByBookerIdAndStatusEquals(MyPageRequest.of(0, 20), bookerId1, BookingStatus.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByBookerIdAndStatusEqualsWhenStatusIsRejectedAndThenReturnedListOfRejectedBookingTest() {
        Booking booking5 = new Booking(6L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking5);
        Long bookerId1 = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByBookerIdAndStatusEquals(MyPageRequest.of(0, 20), bookerId1, BookingStatus.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdOrderByStartDescTest() {
        Booking booking6 = new Booking(7L, time.plusDays(5), time.plusDays(15), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking6);
        Long ownerId1 = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdOrderByStartDesc(MyPageRequest.of(0, 20), ownerId1);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfterTest() {
        Booking booking7 = new Booking(8L, time.minusDays(5), time.plusDays(15), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking7);
        Long ownerId1 = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfter(MyPageRequest.of(0, 20), ownerId1, time, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdAndEndIsBeforeTest() {
        Booking booking8 = new Booking(9L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking8);
        Long ownerId1 = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdAndEndIsBefore(MyPageRequest.of(0, 20), ownerId1, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdAndStartIsAfterOrderByStartDescTest() {
        Booking booking9 = new Booking(10L, time.plusDays(5), time.plusDays(10), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking9);
        Long ownerId1 = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdAndStartIsAfterOrderByStartDesc(MyPageRequest.of(0, 20), ownerId1, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdAndStatusEqualsWhenStatusIsWaitingAndThenReturnedListOfWaitingBookingTest() {
        Booking booking10 = new Booking(11L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking10);
        Long ownerId1 = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdAndStatusEquals(MyPageRequest.of(0, 20), ownerId1, BookingStatus.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdAndStatusEqualsWhenStatusIsRejectedAndThenReturnedListOfRejectedBookingTest() {
        Booking booking11 = new Booking(12L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking11);
        Long ownerId1 = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdAndStatusEquals(MyPageRequest.of(0, 20), ownerId1, BookingStatus.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findFirstByItemIdAndEndIsBeforeOrderByEndDescTest() {
        Booking booking12 = new Booking(13L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking12);
        Long itemId1 = savedBooking.getItem().getId();
        Booking bookingFromRepository = bookingRepository
                .findFirstByItemIdAndEndIsBeforeOrderByEndDesc(itemId1, time).get();

        assertNotNull(bookingFromRepository);
        assertEquals(savedBooking, bookingFromRepository);
    }

    @Test
    void findFirstByItemIdAndStartIsAfterTest() {
        Booking booking13 = new Booking(14L, time.plusDays(5), time.plusDays(15), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking13);
        Long itemId1 = savedBooking.getItem().getId();
        Booking bookingFromRepository = bookingRepository
                .findFirstByItemIdAndStartIsAfter(itemId1, time).get();

        assertNotNull(bookingFromRepository);
        assertEquals(savedBooking, bookingFromRepository);
    }

    @Test
    void existsByBookerIdAndItemIdAndEndIsBeforeTest() {
        Booking booking14 = new Booking(15L, time.minusDays(10), time.minusDays(5), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking14);
        Long bookerId1 = savedBooking.getBooker().getId();
        Long itemId1 = savedBooking.getItem().getId();

        boolean bookingIsExists = bookingRepository
                .existsByBookerIdAndItemIdAndEndIsBefore(bookerId1, itemId1, LocalDateTime.now());

        assertTrue(bookingIsExists);
    }
}