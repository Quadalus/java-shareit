package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentDtoMapperTest {
    private User user;
    private Item item;
    private Comment comment;
    private LocalDateTime now;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "name", "w@email.com");
        item = new Item(1L, "name", "desc", Boolean.TRUE, user, null);
        comment = new Comment(1L, "text", item, user, now);
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
    void toCommentDto() {
        CommentDto commentDto = CommentDtoMapper.toCommentDto(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreatedTime());
    }

    @Test
    void toComment() {
        CommentDtoFromRequest commentDto = new CommentDtoFromRequest("text");

        Comment commentFromDto = CommentDtoMapper.toComment(commentDto, item, user);

        assertNotNull(commentFromDto);
        assertEquals(comment.getText(), commentFromDto.getText());
        assertEquals(comment.getAuthor(), commentFromDto.getAuthor());
        assertEquals(comment.getItem(), commentFromDto.getItem());
    }
}