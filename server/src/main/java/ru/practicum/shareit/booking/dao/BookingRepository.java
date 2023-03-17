package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingByBookerIdOrderByStartDesc(
            Pageable pageable, Long bookerId);

    List<Booking> findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(
            Pageable pageable, Long bookerId, LocalDateTime starTime, LocalDateTime endTime);

    List<Booking> findBookingsByBookerIdAndEndIsBefore(
            Pageable pageable, Long bookerId, LocalDateTime endTime);

    List<Booking> findBookingsByBookerIdAndStartIsAfterOrderByStartDesc(
            Pageable pageable, Long bookerId, LocalDateTime startTime);

    List<Booking> findBookingsByBookerIdAndStatusEquals(
            Pageable pageable, Long bookerId, BookingStatus status);

    List<Booking> findBookingsByItemOwnerIdOrderByStartDesc(
            Pageable pageable, Long ownerId);

    List<Booking> findBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            Pageable pageable, Long ownerId, LocalDateTime startTime, LocalDateTime endTime);

    List<Booking> findBookingsByItemOwnerIdAndEndIsBefore(
            Pageable pageable, Long ownerId, LocalDateTime endTime);

    List<Booking> findBookingsByItemOwnerIdAndStartIsAfterOrderByStartDesc(
            Pageable pageable, Long ownerId, LocalDateTime startTime);

    List<Booking> findBookingsByItemOwnerIdAndStatusEquals(
            Pageable pageable, Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByEndDesc(
            Long itemId, LocalDateTime endTime);

    Optional<Booking> findFirstByItemIdAndStartIsAfter(
            Long itemId, LocalDateTime startTime);

    boolean existsByBookerIdAndItemIdAndEndIsBefore(
            Long bookerId, Long itemId, LocalDateTime endTime);
}