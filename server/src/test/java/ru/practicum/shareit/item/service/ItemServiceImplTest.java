package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.item.exception.NoValidUserToCommentException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookingLastId;
    private Long bookingNextId;
    private Long userId;
    private Long itemId;
    private Long itemRequestId;
    private MyPageRequest pageable;
    private User user;
    private ItemRequest request;
    private Item item;
    private Item itemWithRequest;
    private ItemDtoFromRequest itemDtoFromRequest;
    private ItemDtoFromRequest itemDtoFromRequestWithRequest;

    @BeforeEach
    public void setUp() {
        bookingLastId = 1L;
        bookingNextId = 2L;
        userId = 1L;
        itemId = 1L;
        itemRequestId = 1L;
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(5);
        user = new User(userId, "name", "e@email.com");
        request = new ItemRequest(itemRequestId, "description", user, LocalDateTime.now());
        item = new Item(itemId, "name", "description", Boolean.TRUE, user, null);
        itemWithRequest = new Item(itemId, "name", "description", Boolean.TRUE, user, request);
        itemDtoFromRequest = new ItemDtoFromRequest("name", "description", Boolean.TRUE, null);
        itemDtoFromRequestWithRequest = new ItemDtoFromRequest("name", "description", Boolean.TRUE, itemRequestId);
        pageable = MyPageRequest.of(0, 10);
    }

    @AfterEach
    public void tearDown() {
        item = null;
        itemWithRequest = null;
        itemDtoFromRequest = null;
        itemDtoFromRequestWithRequest = null;
    }

    @Test
    void saveItemWhenPositiveCaseAndThenSaveItemAndReturnedItemDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Item item = ItemDtoMapper.toItemFromDto(itemDtoFromRequest);
        item.setOwner(user);
        when(itemRepository.save(item)).thenReturn(item);

        itemRepository.save(item);
        ItemDto itemDto = itemService.saveItem(itemDtoFromRequest, userId);

        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getIsAvailable());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void saveItemWhenUserNotFoundAndThenThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.saveItem(itemDtoFromRequest, userId));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(0)).save(item);
    }

    @Test
    void saveItemWhenItemRequestExistsAndThenSaveItemAndReturnedItemDtoWithItemRequest() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Item item = ItemDtoMapper.toItemFromDto(itemDtoFromRequestWithRequest);
        when(itemRequestRepository.getReferenceById(itemRequestId)).thenReturn(request);
        itemRequestRepository.getReferenceById(itemRequestId);

        item.setOwner(user);
        when(itemRepository.save(itemWithRequest)).thenReturn(itemWithRequest);

        itemRepository.save(itemWithRequest);
        ItemDto itemDto = itemService.saveItem(itemDtoFromRequestWithRequest, userId);

        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getIsAvailable());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateItemWhenPositiveCaseAndThenUpdateItemAndReturnedItemDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Item item = ItemDtoMapper.toItemFromDto(itemDtoFromRequest);
        when(itemRepository.save(item)).thenReturn(new Item());

        itemRepository.save(item);
        ItemDto itemDto = itemService.updateItem(itemDtoFromRequest, itemId, userId);

        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getIsAvailable());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItemWhenUserNotFoundAndThenThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(itemDtoFromRequest, itemId, userId));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(0)).findById(itemId);
        verify(itemRepository, times(0)).save(item);
    }

    @Test
    void updateItemWhenItemNotFoundAndThenThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(itemDtoFromRequest, itemId, userId));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(0)).save(item);
    }

    @Test
    void updateItemWhenOwnerNotValidForItemAndThenThrowNotFoundException() {
        User owner = new User(2L, "name", "wrqe@email.com");
        Item itemWithWrongOwner = new Item(itemId, "name", "description", Boolean.TRUE, owner, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemWithWrongOwner));

        IncorrectParameterException incorrectParameterException = assertThrows(IncorrectParameterException.class,
                () -> itemService.updateItem(itemDtoFromRequest, itemId, userId));

        assertEquals(IncorrectParameterException.class, incorrectParameterException.getClass());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(0)).save(item);
    }

    @Test
    void deleteItemWhenPositiveCaseAndThenDeleteItem() {
        itemService.deleteItem(itemId);
        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void getItemByIdWhenPositiveCaseAndWithShortBookingAndThenReturnedItemDetailedDto() {
        List<Comment> comments = List.of(createComment());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId)).thenReturn(comments);
        when(bookingRepository.findFirstByItemIdAndEndIsBeforeOrderByEndDesc(anyLong(), any()))
                .thenReturn(Optional.of(createBooking(bookingLastId)));
        when(bookingRepository.findFirstByItemIdAndStartIsAfter(anyLong(), any()))
                .thenReturn(Optional.of(createBooking(bookingNextId)));
        ItemDetailedDto itemDetailedDto = itemService.getItemById(itemId, userId);

        assertNotNull(item);
        assertEquals(item.getName(), itemDetailedDto.getName());
        assertEquals(item.getDescription(), itemDetailedDto.getDescription());
        assertEquals(item.getId(), itemDetailedDto.getId());
        assertNotNull(itemDetailedDto.getLastBooking());
        assertNotNull(itemDetailedDto.getNextBooking());
    }

    @Test
    void getItemByIdWhenPositiveCaseAndWithoutShortBookingsAndThenReturnedItemDetailedDtoWithoutShortBookings() {
        List<Comment> comments = List.of(createComment());
        User owner = new User(2L, "name", "wrqe@email.com");
        Item itemWithWrongOwner = new Item(itemId, "name", "description", Boolean.TRUE, owner, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemWithWrongOwner));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId)).thenReturn(comments);

        bookingRepository.findFirstByItemIdAndEndIsBeforeOrderByEndDesc(itemId, start);
        bookingRepository.findFirstByItemIdAndStartIsAfter(itemId, start);
        ItemDetailedDto itemDetailedDto = itemService.getItemById(itemId, userId);

        assertNotNull(itemDetailedDto);
        assertEquals(item.getName(), itemDetailedDto.getName());
        assertEquals(item.getDescription(), itemDetailedDto.getDescription());
        assertEquals(item.getId(), itemDetailedDto.getId());
        assertNull(itemDetailedDto.getLastBooking());
        assertNull(itemDetailedDto.getNextBooking());
    }

    @Test
    void getUserItemsByIdWhenPositiveCaseAndReturnedListOfItemDetailedDto() {
        List<Item> items = List.of(item);
        when(itemRepository.findAllByOwnerId(pageable, userId)).thenReturn(items);

        List<ItemDetailedDto> itemDetailedDto = itemService.getUserItemsById(pageable, userId);

        assertNotNull(item);
        assertEquals(1, itemDetailedDto.size());

        verify(itemRepository, times(1)).findAllByOwnerId(pageable, userId);
    }

    @Test
    void getUserItemByTextWhenSearchTextIsEmptyAndReturnedListOfItemDto() {
        String name = "";
        List<ItemDto> itemDetailedDto = itemService.getUserItemByText(pageable, userId, name);

        assertNotNull(item);
        assertEquals(0, itemDetailedDto.size());

        verify(itemRepository, times(0)).findItemsByText(pageable, name);
    }

    @Test
    void getUserItemByTextWhenPositiveCaseAndReturnedListOfItemDto() {
        List<Item> items = List.of(item);
        String name = "name";
        when(itemRepository.findItemsByText(pageable, name)).thenReturn(items);

        List<ItemDto> itemDetailedDto = itemService.getUserItemByText(pageable, userId, name);

        assertNotNull(item);
        assertEquals(1, itemDetailedDto.size());

        verify(itemRepository, times(1)).findItemsByText(pageable, name);
    }

    @Test
    void addCommentToItemWhenItemNotFoundAndThenThrowNotFoundException() {
        CommentDtoFromRequest commentDtoFromRequest = new CommentDtoFromRequest("text");

        when(bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any())).thenReturn(Boolean.TRUE);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.addCommentToItem(userId, itemId, commentDtoFromRequest));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(bookingRepository, times(1)).existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void addCommentToItemWhenUserNotFoundAndThenThrowNotFoundException() {
        CommentDtoFromRequest commentDtoFromRequest = new CommentDtoFromRequest("text");

        when(bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any())).thenReturn(Boolean.TRUE);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.addCommentToItem(userId, itemId, commentDtoFromRequest));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(bookingRepository, times(1)).existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(0)).findById(anyLong());
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void addCommentToItem() {
        CommentDtoFromRequest commentDtoFromRequest = new CommentDtoFromRequest("text");
        Comment comment = CommentDtoMapper.toComment(commentDtoFromRequest, item, user);

        when(bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any())).thenReturn(Boolean.TRUE);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDto = itemService.addCommentToItem(userId, itemId, commentDtoFromRequest);

        assertNotNull(commentDto);
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreatedTime());

        verify(bookingRepository, times(1)).existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addCommentToItemWhenUserNotValidBookingItemAndThenThrowNoValidUserToCommentException() {
        CommentDtoFromRequest commentDtoFromRequest = new CommentDtoFromRequest("text");

        when(bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any())).thenReturn(Boolean.FALSE);
        NoValidUserToCommentException noValidUserToCommentException = assertThrows(NoValidUserToCommentException.class,
                () -> itemService.addCommentToItem(userId, itemId, commentDtoFromRequest));

        assertEquals(NoValidUserToCommentException.class, noValidUserToCommentException.getClass());
        verify(bookingRepository, times(1)).existsByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any());
        verify(userRepository, times(0)).findById(anyLong());
        verify(itemRepository, times(0)).findById(anyLong());
        verify(commentRepository, times(0)).save(any());
    }

    private Booking createBooking(Long bookingId) {
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    private Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText("text");
        comment.setCreated(start);

        return comment;
    }
}