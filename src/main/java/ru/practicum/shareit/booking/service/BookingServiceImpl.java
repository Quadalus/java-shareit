package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(Long userId, BookingDtoFromRequest bookingDto) {
        Long itemId = bookingDto.getItemId();
        Item item = getItem(itemId);
        User user = getUser(userId);
        Booking booking = BookingDtoMapper.fromBookingDto(bookingDto, user, item);

        checkUserIsNotOwner(item, userId);
        checkItemForAvailable(item);
        checkBookingTime(bookingDto.getStart(), bookingDto.getEnd());
        bookingRepository.save(booking);
        return BookingDtoMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto bookingConfirmation(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("booking with id = %d not found", bookingId)));

        checkValidOwner(userId);
        checkValidOwnerToItem(booking.getItem(), userId);
        checkBookingStatusNotApprove(booking);
        booking.setStatus((isApproved == Boolean.TRUE) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingDtoMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        checkValidOwner(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("booking with id = %d not found", bookingId)));
        checkOwnerOrBooker(booking, userId);
        return BookingDtoMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBooker(Long userId, String state) {
        checkValidOwner(userId);
        switch (getState(state)) {
            case ALL:
                return getAllBookingsByBookerId(userId);
            case CURRENT:
                return getCurrentBookingsByBookerId(userId);
            case PAST:
                return getPastBookingsByBookerId(userId);
            case FUTURE:
                return getFutureBookingsByBookerId(userId);
            case WAITING:
                return getWaitingBookingsByBookerId(userId);
            case REJECTED:
                return getRejectedBookingsByBookerId(userId);
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String state) {
        checkValidOwner(userId);
        switch (getState(state)) {
            case ALL:
                return getAllBookingsByOwnerId(userId);
            case CURRENT:
                return getCurrentBookingsByOwnerId(userId);
            case PAST:
                return getPastBookingsByOwnerId(userId);
            case FUTURE:
                return getFutureBookingsByOwnerId(userId);
            case WAITING:
                return getWaitingBookingsByOwnerId(userId);
            case REJECTED:
                return getRejectedBookingsByOwnerId(userId);
            default:
                throw new RuntimeException();
        }
    }


    private void checkValidOwner(Long ownerId) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException(String.format("user with id=%d not found", ownerId));
        }
    }

    private void checkValidOwnerToItem(Item item, Long ownerId) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new IncorrectParameterException("this user doesn't have this item");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id=%d not found", userId)));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("item with id=%d not found", itemId)));
    }

    private void checkItemForAvailable(Item item) {
        if (!item.getIsAvailable()) {
            throw new ItemNotAvailableException(String.format("item with id = %d not available.", item.getId()));
        }
    }

    private void checkBookingTime(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IncorrectBookingTimeException(
                    String.format("end time must be after the start: end %s >---< start %s", end, start)
            );
        }
    }

    private void checkBookingExists(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingNotFoundException(String.format("booking with id = %d not found", bookingId));
        }
    }

    private void checkUserIsNotOwner(Item item, Long ownerId) {
        Long itemId = item.getId();
        if (item.getOwner().getId().equals(ownerId)) {
            throw new UserAlreadyItemOwnerException(
                    String.format("this user with id = %d is already the owner of the item with id = %d", ownerId, itemId)
            );
        }
    }

    private void checkBookingStatusNotApprove(Booking booking) {
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingAlreadyApprovedException(String.format("booking with id = %d already approved", booking.getId()));
        }
    }

    private void checkOwnerOrBooker(Booking booking, Long userId) {
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        boolean isOwner = ownerId.equals(userId);
        boolean isBooker = bookerId.equals(userId);

        if (!isOwner && !isBooker) {
            throw new BookingNotFoundException(
                    String.format("Booking with id = %d or owner with booker ID_%d not found", booking.getId(),
                            userId)
            );
        }
    }

    private State getState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException ex) {
            throw new IllegalBookingStateException(String.format(state));
        }
    }

    private List<BookingDto> getAllBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingByBookerIdOrderByStartDesc(bookerId)
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getCurrentBookingsByBookerId(Long bookerId) {
        LocalDateTime time = LocalDateTime.now();
        return bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(bookerId, time, time)
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getPastBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBookerIdAndEndIsBefore(bookerId, LocalDateTime.now())
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getFutureBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, LocalDateTime.now())
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getWaitingBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBookerIdAndStatusEquals(bookerId, BookingStatus.WAITING)
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getRejectedBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBookerIdAndStatusEquals(bookerId, BookingStatus.REJECTED)
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getAllBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(ownerId)
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getCurrentBookingsByOwnerId(Long ownerId) {
        LocalDateTime time = LocalDateTime.now();
        return bookingRepository.findBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId, time, time)
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getPastBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdAndEndIsBefore(ownerId, LocalDateTime.now())
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getFutureBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, LocalDateTime.now())
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getWaitingBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(ownerId, BookingStatus.WAITING)
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getRejectedBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(ownerId, BookingStatus.REJECTED)
                .stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
