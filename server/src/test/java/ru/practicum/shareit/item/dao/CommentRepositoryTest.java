package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    private final LocalDateTime time = LocalDateTime.now();
    private long autoIncrementItemId = 1L;
    private long autoIncrementUserId = 1L;
    private long autoIncrementCommentId = 1L;

    @Test
    void findAllByItemIdOrderByCreatedDesc() {
        User user = new User(autoIncrementUserId++, "name", "w@email.com");
        Item item = new Item(autoIncrementItemId++, "name", "desc", Boolean.TRUE, user, null);
        Comment comment = new Comment(autoIncrementCommentId++, "text", item, user, time);
        Comment savedComment = commentRepository.save(comment);

        List<Comment> comments = commentRepository
                .findAllByItemIdOrderByCreatedDesc(savedComment.getItem().getId());

        assertEquals(comments.size(), 1);
        assertEquals(savedComment, comments.get(0));
    }
}