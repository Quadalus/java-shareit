package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    @Test
    public void twoCommentMustBeEquals() {
        User user = new User(1L, "name", "e@email.com");
        Item item = new Item(1L, "name", "desc", Boolean.TRUE, user,null);
        Comment comment = new Comment(1L, "text", item, user, LocalDateTime.now());
        Comment comment1 = new Comment(1L, "text", item, user, LocalDateTime.now());

        assertEquals(comment, comment1);
        assertEquals(comment, comment);
        assertEquals(comment.hashCode(), comment.hashCode());
    }
}