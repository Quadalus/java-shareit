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
    /* Комментарий для ревьювера
    Тут booking не вынес в @BeforeEach,
    так как у каждого booking отличается время начала или время окончания или статус booking*/
    @Autowired
    private BookingRepository bookingRepository;

    private final Long itemId = 1L;
    private final LocalDateTime time = LocalDateTime.now();
    private final User owner = new User(2L, "ownerName", "owner@email.com");
    private final User booker = new User(1L, "bookerName", "booker@email.com");
    private final Item item = new Item(itemId, "name", "description", Boolean.TRUE, owner, null);

    @AfterEach
    public void tearDown() {
        bookingRepository.deleteAll();
    }

    @Test
    void findBookingByBookerIdOrderByStartDescTest() {
        Booking booking = new Booking(1L, time.plusDays(5), time.plusDays(15), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long bookerId = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingByBookerIdOrderByStartDesc(MyPageRequest.of(0, 5), bookerId);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));

    }

    @Test
    void findBookingByBookerIdAndStartIsBeforeAndEndIsAfterTest() {
        Booking booking = new Booking(2L, time.minusDays(5), time.plusDays(15), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long bookerId = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(MyPageRequest.of(0, 20), bookerId, time, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByBookerIdAndEndIsBeforeTest() {
        Booking booking = new Booking(3L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long bookerId = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByBookerIdAndEndIsBefore(MyPageRequest.of(0, 20), bookerId, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByBookerIdAndStartIsAfterOrderByStartDescTest() {
        Booking booking = new Booking(4L, time.plusDays(5), time.plusDays(12), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long bookerId = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByBookerIdAndStartIsAfterOrderByStartDesc(MyPageRequest.of(0, 20), bookerId, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByBookerIdAndStatusEqualsWhenStatusIsWaitingAndThenReturnedListOfWaitingBookingTest() {
        Booking booking = new Booking(5L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long bookerId = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByBookerIdAndStatusEquals(MyPageRequest.of(0, 20), bookerId, BookingStatus.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByBookerIdAndStatusEqualsWhenStatusIsRejectedAndThenReturnedListOfRejectedBookingTest() {
        Booking booking = new Booking(6L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        Long bookerId = savedBooking.getBooker().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByBookerIdAndStatusEquals(MyPageRequest.of(0, 20), bookerId, BookingStatus.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdOrderByStartDescTest() {
        Booking booking = new Booking(7L, time.plusDays(5), time.plusDays(15), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long ownerId = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdOrderByStartDesc(MyPageRequest.of(0, 20), ownerId);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfterTest() {
        Booking booking = new Booking(8L, time.minusDays(5), time.plusDays(15), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long ownerId = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfter(MyPageRequest.of(0, 20), ownerId, time, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdAndEndIsBeforeTest() {
        Booking booking = new Booking(9L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long ownerId = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdAndEndIsBefore(MyPageRequest.of(0, 20), ownerId, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdAndStartIsAfterOrderByStartDescTest() {
        Booking booking = new Booking(10L, time.plusDays(5), time.plusDays(10), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long ownerId = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdAndStartIsAfterOrderByStartDesc(MyPageRequest.of(0, 20), ownerId, time);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdAndStatusEqualsWhenStatusIsWaitingAndThenReturnedListOfWaitingBookingTest() {
        Booking booking = new Booking(11L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long ownerId = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdAndStatusEquals(MyPageRequest.of(0, 20), ownerId, BookingStatus.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findBookingsByItemOwnerIdAndStatusEqualsWhenStatusIsRejectedAndThenReturnedListOfRejectedBookingTest() {
        Booking booking = new Booking(12L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        Long ownerId = savedBooking.getItem().getOwner().getId();
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerIdAndStatusEquals(MyPageRequest.of(0, 20), ownerId, BookingStatus.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(savedBooking, bookings.get(0));
    }

    @Test
    void findFirstByItemIdAndEndIsBeforeOrderByEndDescTest() {
        Booking booking = new Booking(13L, time.minusDays(5), time.minusDays(4), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long itemId = savedBooking.getItem().getId();
        Booking bookingFromRepository = bookingRepository
                .findFirstByItemIdAndEndIsBeforeOrderByEndDesc(itemId, time).get();

        assertNotNull(bookingFromRepository);
        assertEquals(savedBooking, bookingFromRepository);
    }

    @Test
    void findFirstByItemIdAndStartIsAfterTest() {
        Booking booking = new Booking(14L, time.plusDays(5), time.plusDays(15), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long itemId = savedBooking.getItem().getId();
        Booking bookingFromRepository = bookingRepository
                .findFirstByItemIdAndStartIsAfter(itemId, time).get();

        assertNotNull(bookingFromRepository);
        assertEquals(savedBooking, bookingFromRepository);
    }

    @Test
    void existsByBookerIdAndItemIdAndEndIsBeforeTest() {
        Booking booking = new Booking(15L, time.minusDays(10), time.minusDays(5), item, booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        Long bookerId = savedBooking.getBooker().getId();
        Long itemId = savedBooking.getItem().getId();

        boolean bookingIsExists = bookingRepository
                .existsByBookerIdAndItemIdAndEndIsBefore(bookerId, itemId, LocalDateTime.now());

        assertTrue(bookingIsExists);
    }
}