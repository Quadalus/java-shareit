package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    private final Long itemId = 1L;
    private final Long userId = 1L;
    private final Long ownerId = 2L;
    private final Long bookingId = 1L;
    private User owner;
    private User user;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setId(ownerId);
        owner.setName("owner name");
        owner.setEmail("owner@email.com");

        user = new User();
        user.setId(userId);
        user.setName("user name");
        user.setEmail("user@email.com");

        item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        item.setName("item name");
        item.setDescription("item description");
        item.setIsAvailable(true);

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(30);
    }

    @AfterEach
    public void tearDown() {
        owner = null;
        user = null;
        item = null;
    }

    @Test
    void addBookingWhenPositiveCaseAndThenAddBookingAndReturnedBookingDto() {
        BookingDtoFromRequest bookingDto = new BookingDtoFromRequest(item.getId(), start, end);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Item item1 = itemRepository.findById(itemId).get();
        User user1 = userRepository.findById(userId).get();
        Booking booking = BookingDtoMapper.fromBookingDto(bookingDto, user1, item1);
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingRepository.save(booking);
        BookingDto bookingDto1 = bookingService.addBooking(userId, bookingDto);

        assertNotNull(bookingDto1);
        assertEquals(booking.getId(), bookingDto1.getId());
        assertEquals(booking.getStart(), bookingDto1.getStart());
        assertEquals(booking.getEnd(), bookingDto1.getEnd());

        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void addBookingWhenItemNotFoundAndThenThrowNotFoundException() {
        BookingDtoFromRequest bookingDto = new BookingDtoFromRequest(item.getId(), start, end);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User user1 = userRepository.findById(userId).get();
        Booking booking = BookingDtoMapper.fromBookingDto(bookingDto, user1, item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingRepository.save(booking);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(userId, bookingDto));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, never()).findById(userId);
        inOrder.verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBookingWhenUserNotFoundAndThenThrowNotFoundException() {
        BookingDtoFromRequest bookingDto = new BookingDtoFromRequest(item.getId(), start, end);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Item item1 = itemRepository.findById(itemId).get();
        Booking booking = BookingDtoMapper.fromBookingDto(bookingDto, user, item1);
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingRepository.save(booking);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(userId, bookingDto));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBookingWhenUserIsOwnerAndThenThrowUserAlreadyItemOwnerException() {
        BookingDtoFromRequest bookingDto = new BookingDtoFromRequest(item.getId(), start, end);
        item.setOwner(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Booking booking = BookingDtoMapper.fromBookingDto(bookingDto, user, item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingRepository.save(booking);
        UserAlreadyItemOwnerException userAlreadyItemOwnerException = assertThrows(UserAlreadyItemOwnerException.class,
                () -> bookingService.addBooking(userId, bookingDto));

        assertEquals(UserAlreadyItemOwnerException.class, userAlreadyItemOwnerException.getClass());
        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBookingWhenItemIsNotAvailableAndItemNotAvailableException() {
        BookingDtoFromRequest bookingDto = new BookingDtoFromRequest(item.getId(), start, end);
        item.setIsAvailable(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Booking booking = BookingDtoMapper.fromBookingDto(bookingDto, user, item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingRepository.save(booking);
        ItemNotAvailableException itemNotAvailableException = assertThrows(ItemNotAvailableException.class,
                () -> bookingService.addBooking(userId, bookingDto));

        assertEquals(ItemNotAvailableException.class, itemNotAvailableException.getClass());
        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(bookingRepository, never()).save(booking);
    }

    @Test
    void bookingConfirmationWhenPositiveCaseAndThenBookingStatusIsApprovedAndReturnedBookingDto() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto bookingDto = bookingService.bookingConfirmation(ownerId, bookingId, Boolean.TRUE);
        assertNotNull(bookingDto);
        assertEquals(bookingDto.getStatus(), BookingStatus.APPROVED);

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
    }

    @Test
    void bookingConfirmationWhenPositiveCaseAndThenBookingStatusIsRejectedAndReturnedBookingDto() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto bookingDto = bookingService.bookingConfirmation(ownerId, bookingId, Boolean.FALSE);
        assertNotNull(bookingDto);
        assertEquals(bookingDto.getStatus(), BookingStatus.REJECTED);

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
    }

    @Test
    void bookingConfirmationWhenBookingNotFoundAndThenThrowBookingNotFoundException() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        BookingNotFoundException bookingNotFoundException = assertThrows(BookingNotFoundException.class,
                () -> bookingService.bookingConfirmation(ownerId, bookingId, Boolean.TRUE));

        assertEquals(BookingNotFoundException.class, bookingNotFoundException.getClass());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, never()).findById(ownerId);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void bookingConfirmationWhenBookingOwnerIsNotValidAndThenThrowBookingNotFoundException() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.bookingConfirmation(ownerId, bookingId, Boolean.TRUE));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void bookingConfirmationWhenBookingOwnerIsNotValidToItemAndThenThrowBookingNotFoundException() {
        item.setOwner(user);
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));

        IncorrectParameterException incorrectParameterException = assertThrows(IncorrectParameterException.class,
                () -> bookingService.bookingConfirmation(ownerId, bookingId, Boolean.TRUE));

        assertEquals(IncorrectParameterException.class, incorrectParameterException.getClass());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void bookingConfirmationWhenBookingAlreadyApprovedAndThenThrowBookingAlreadyApprovedException() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));

        BookingAlreadyApprovedException bookingAlreadyApprovedException = assertThrows(BookingAlreadyApprovedException.class,
                () -> bookingService.bookingConfirmation(ownerId, bookingId, Boolean.TRUE));

        assertEquals(BookingAlreadyApprovedException.class, bookingAlreadyApprovedException.getClass());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getBookingByIdWhenPositiveCaseAndThenBookingGotAndReturnedBookingDto() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getBookingById(ownerId, bookingId);
        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());

        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingByIdWhenUserNotFoundAndThenThrowBookingNotFoundException() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(ownerId, bookingId));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, never()).findById(bookingId);
    }

    @Test
    void getBookingByIdWhenBookingNotFoundAndThenThrowBookingNotFoundException() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        BookingNotFoundException bookingNotFoundException = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(ownerId, bookingId));

        assertEquals(BookingNotFoundException.class, bookingNotFoundException.getClass());
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingByIdWhenOwnerOrBookerNotFoundAndThenThrowBookingNotFoundException() {
        item.setOwner(user);
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingNotFoundException bookingNotFoundException = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(ownerId, bookingId));

        assertEquals(BookingNotFoundException.class, bookingNotFoundException.getClass());
        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingsByBookerWhenPositiveCaseAndStateIsAllAndThenReturnedListOfAllBookings() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        Booking booking1 = new Booking(bookingId, start, end, item, user, BookingStatus.REJECTED);
        List<Booking> bookings = List.of(booking, booking1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdOrderByStartDesc(any(Pageable.class), anyLong()))
                .thenReturn(bookings);

        List<BookingDto> bookings1 = bookingService.getBookingsByBooker(Pageable.ofSize(20), userId, "ALL");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
    }

    @Test
    void getBookingsByBookerWhenPositiveCaseAndStateIsCurrentAndThenReturnedListOfCurrentBookings() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        Booking booking1 = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        List<Booking> bookings = List.of(booking, booking1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(any(Pageable.class), anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bookings);
        List<BookingDto> bookings1 = bookingService.getBookingsByBooker(MyPageRequest.of(0, 20), userId, "CURRENT");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
    }

    @Test
    void getBookingsByBookerWhenPositiveCaseAndStateIsPASTAndThenReturnedListOfPASTBookings() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        Booking booking1 = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        List<Booking> bookings = List.of(booking, booking1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerIdAndEndIsBefore(any(Pageable.class), anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookings);
        List<BookingDto> bookings1 = bookingService.getBookingsByBooker(MyPageRequest.of(0, 20), userId, "PAST");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
    }

    @Test
    void getBookingsByBookerWhenPositiveCaseAndStateIsFutureAndThenReturnedListOfFutureBookings() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        Booking booking1 = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        List<Booking> bookings = List.of(booking, booking1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerIdAndStartIsAfterOrderByStartDesc(any(Pageable.class), anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookings);
        List<BookingDto> bookings1 = bookingService.getBookingsByBooker(MyPageRequest.of(0, 20), userId, "FUTURE");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
    }

    @Test
    void getBookingsByBookerWhenPositiveCaseAndStateIsWaitingAndThenReturnedListOfWaitingBookings() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(MyPageRequest.of(0, 20), userId, BookingStatus.WAITING))
                .thenReturn(bookings);
        List<BookingDto> bookings1 = bookingService.getBookingsByBooker(MyPageRequest.of(0, 20), userId, "WAITING");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
    }

    @Test
    void getBookingsByBookerWhenPositiveCaseAndStateIsRejectedAndThenReturnedListOfRejectedBookings() {
        Booking booking1 = new Booking(bookingId, start, end, item, user, BookingStatus.REJECTED);
        List<Booking> bookings = List.of(booking1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(MyPageRequest.of(0, 20), userId, BookingStatus.REJECTED))
                .thenReturn(bookings);
        List<BookingDto> bookings1 = bookingService.getBookingsByBooker(MyPageRequest.of(0, 20), userId, "REJECTED");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
    }

    @Test
    void getBookingsByBookerWhenStateIsWrongAndThenThrowIllegalBookingStateException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        IllegalBookingStateException illegalBookingStateException = assertThrows(IllegalBookingStateException.class,
                () -> bookingService.getBookingsByBooker(MyPageRequest.of(0, 20), userId, "WRONG"));

        assertEquals(IllegalBookingStateException.class, illegalBookingStateException.getClass());
    }

    @Test
    void getBookingsByBookerWhenOwnerIsNotValidAndThenNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByBooker(MyPageRequest.of(0, 20), userId, "ALL"));

        assertEquals(NotFoundException.class, notFoundException.getClass());
    }

    @Test
    void getBookingsByOwnerWhenPositiveCaseAndStateIsAllAndThenReturnedListOfAllBookings() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        Booking booking1 = new Booking(bookingId, start, end, item, user, BookingStatus.REJECTED);
        List<Booking> bookings = List.of(booking, booking1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(any(Pageable.class), anyLong()))
                .thenReturn(bookings);

        List<BookingDto> bookings1 = bookingService.getBookingsByOwner(Pageable.ofSize(20), userId, "ALL");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
    }

    @Test
    void getBookingsByOwnerWhenPositiveCaseAndStateIsCurrentAndThenReturnedListOfCurrentBookings() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        Booking booking1 = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        List<Booking> bookings = List.of(booking, booking1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfter(any(Pageable.class), anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bookings);
        List<BookingDto> bookings1 = bookingService.getBookingsByOwner(MyPageRequest.of(0, 20), userId, "CURRENT");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
        assertEquals(bookings1.size(), 2);
    }

    @Test
    void getBookingsByOwnerWhenPositiveCaseAndStateIsPASTAndThenReturnedListOfPASTBookings() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        Booking booking1 = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        List<Booking> bookings = List.of(booking, booking1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByItemOwnerIdAndEndIsBefore(any(Pageable.class), anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookings);
        List<BookingDto> bookings1 = bookingService.getBookingsByOwner(MyPageRequest.of(0, 20), userId, "PAST");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
        assertEquals(bookings1.size(), 2);
    }

    @Test
    void getBookingsByOwnerWhenPositiveCaseAndStateIsFutureAndThenReturnedListOfFutureBookings() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        Booking booking1 = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        List<Booking> bookings = List.of(booking, booking1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByItemOwnerIdAndStartIsAfterOrderByStartDesc(any(Pageable.class), anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookings);
        List<BookingDto> bookings1 = bookingService.getBookingsByOwner(MyPageRequest.of(0, 20), userId, "FUTURE");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
        assertEquals(bookings1.size(), 2);
    }

    @Test
    void getBookingsByOwnerWhenPositiveCaseAndStateIsWaitingAndThenReturnedListOfWaitingBookings() {
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(MyPageRequest.of(0, 20), userId, BookingStatus.WAITING))
                .thenReturn(bookings);
        List<BookingDto> bookings1 = bookingService.getBookingsByOwner(MyPageRequest.of(0, 20), userId, "WAITING");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
        assertEquals(bookings1.size(), 1);
    }

    @Test
    void getBookingsByOwnerWhenPositiveCaseAndStateIsRejectedAndThenReturnedListOfRejectedBookings() {
        Booking booking1 = new Booking(bookingId, start, end, item, user, BookingStatus.REJECTED);
        List<Booking> bookings = List.of(booking1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(MyPageRequest.of(0, 20), userId, BookingStatus.REJECTED))
                .thenReturn(bookings);
        List<BookingDto> bookings1 = bookingService.getBookingsByOwner(MyPageRequest.of(0, 20), userId, "REJECTED");

        assertNotNull(bookings1);
        assertEquals(bookings1.size(), bookings.size());
        assertEquals(bookings1.size(), 1);
    }

    @Test
    void getBookingsByOwnerWhenStateIsWrongAndThenThrowIllegalBookingStateException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        IllegalBookingStateException illegalBookingStateException = assertThrows(IllegalBookingStateException.class,
                () -> bookingService.getBookingsByOwner(MyPageRequest.of(0, 20), userId, "WRONG"));

        assertEquals(IllegalBookingStateException.class, illegalBookingStateException.getClass());
    }

    @Test
    void getBookingsByOwnerWhenOwnerIsNotValidAndThenNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByOwner(MyPageRequest.of(0, 20), userId, "ALL"));

        assertEquals(NotFoundException.class, notFoundException.getClass());
    }
}