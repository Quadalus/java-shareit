package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingByBookerIdOrderByStartDesc(
            Long bookerId);

    List<Booking> findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime starTime, LocalDateTime endTime);

    List<Booking> findBookingsByBookerIdAndEndIsBefore(
            Long bookerId, LocalDateTime endTime);

    List<Booking> findBookingsByBookerIdAndStartIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime startTime);

    List<Booking> findBookingsByBookerIdAndStatusEquals(
            Long bookerId, BookingStatus status);

    List<Booking> findBookingsByItemOwnerIdOrderByStartDesc(
            Long ownerId);

    List<Booking> findBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            Long ownerId, LocalDateTime startTime, LocalDateTime endTime);

    List<Booking> findBookingsByItemOwnerIdAndEndIsBefore(
            Long ownerId, LocalDateTime endTime);

    List<Booking> findBookingsByItemOwnerIdAndStartIsAfterOrderByStartDesc(
            Long ownerId, LocalDateTime startTime);

    List<Booking> findBookingsByItemOwnerIdAndStatusEquals(
            Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByEndDesc(
            Long itemId, LocalDateTime endTime);

    Optional<Booking> findFirstByItemIdAndStartIsAfter(
            Long itemId, LocalDateTime startTime);

    boolean existsByBookerIdAndItemIdAndEndIsBefore(
            Long bookerId, Long itemId, LocalDateTime endTime);
}