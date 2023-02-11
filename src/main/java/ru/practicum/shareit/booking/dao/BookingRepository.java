package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingByBookerIdOrderByStartDesc(
            Long bookerId);

    List<Booking> findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findBookingsByBookerIdAndEndIsBefore(
            Long bookerId, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndStartIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndStatusEquals(
            Long bookerId, BookingStatus status);

    List<Booking> findBookingsByItemOwnerIdOrderByStartDesc(
            Long ownerId);

    List<Booking> findBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            Long ownerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findBookingsByItemOwnerIdAndEndIsBefore(
            Long ownerId, LocalDateTime time);

    List<Booking> findBookingsByItemOwnerIdAndStartIsAfterOrderByStartDesc(
            Long ownerId, LocalDateTime time);

    List<Booking> findBookingsByItemOwnerIdAndStatusEquals(
            Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByEndDesc(
            Long itemId, LocalDateTime time);

    Optional<Booking> findFirstByItemIdAndStartIsAfter(
            Long itemId, LocalDateTime time);

    boolean existsByBookerIdAndItemIdAndEndIsBefore(
            Long bookerId, Long itemId, LocalDateTime time);
}