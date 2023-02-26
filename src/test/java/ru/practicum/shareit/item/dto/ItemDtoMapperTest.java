package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoMapperTest {
    private User user;
    private Item item;
    private Comment comment;
    private LocalDateTime now;

    @BeforeEach
    public void setUp() {
        long id = 1L;
        user = new User(id, "name", "w@email.com");
        item = new Item(id, "name", "description", Boolean.TRUE, user, null);
        comment = new Comment(id, "text", item, user, now);
        now = LocalDateTime.now();
    }

    @AfterEach
    public void tearDown() {
        user = null;
        item = null;
        comment = null;
        now = null;
    }

    @Test
    void toItemDto() {
        ItemDto itemDto = ItemDtoMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getIsAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemDetailedDtoTestWhenTwoArguments() {
        CommentDto commentDto = CommentDtoMapper.toCommentDto(comment);
        List<CommentDto> comments = List.of(commentDto);
        ItemDetailedDto itemDetailedDto = ItemDtoMapper.toItemDetailedDto(item, comments);

        assertNotNull(itemDetailedDto);
        assertNotNull(itemDetailedDto.getComments());
        assertNull(itemDetailedDto.getNextBooking());
        assertNull(itemDetailedDto.getLastBooking());
        assertEquals(item.getId(), itemDetailedDto.getId());
        assertEquals(item.getDescription(), itemDetailedDto.getDescription());
        assertEquals(item.getName(), itemDetailedDto.getName());
        assertEquals(item.getIsAvailable(), itemDetailedDto.getAvailable());
    }

    @Test
    void testToItemDetailedDtoTestWithAllArguments() {
        CommentDto commentDto = CommentDtoMapper.toCommentDto(comment);
        List<CommentDto> comments = List.of(commentDto);
        long bookingId = 1L;
        long bookerId = 1L;
        BookingShortDto bookingShortDto = new BookingShortDto(bookingId, bookerId, now, now);
        ItemDetailedDto itemDetailedDto = ItemDtoMapper.toItemDetailedDto(item, bookingShortDto, bookingShortDto, comments);

        assertNotNull(itemDetailedDto);
        assertNotNull(itemDetailedDto.getNextBooking());
        assertNotNull(itemDetailedDto.getLastBooking());
        assertNotNull(itemDetailedDto.getComments());
        assertEquals(item.getId(), itemDetailedDto.getId());
        assertEquals(item.getDescription(), itemDetailedDto.getDescription());
        assertEquals(item.getName(), itemDetailedDto.getName());
        assertEquals(item.getIsAvailable(), itemDetailedDto.getAvailable());
    }

    @Test
    void toItemFromDtoTest() {
        ItemDtoFromRequest itemDtoFromRequest = new ItemDtoFromRequest("name", "description", Boolean.TRUE, null);
        Item itemFromDto = ItemDtoMapper.toItemFromDto(itemDtoFromRequest);

        assertNotNull(itemFromDto);
        assertEquals(item.getDescription(), itemFromDto.getDescription());
        assertEquals(item.getName(), itemFromDto.getName());
        assertEquals(item.getIsAvailable(), itemFromDto.getIsAvailable());
    }
}