package ru.practicum.shareit.item.dto;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class CommentDtoMapper {
    public static CommentDto toCommentDto(@NonNull Comment comment) {
        return new CommentDto.CommentDtoBuilder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .createdTime(comment.getCreated())
                .build();
    }


    public static Comment toComment(@NonNull CommentDtoFromRequest commentDto, @NonNull Item item, @NonNull User user) {
        Comment comment = new Comment();
        Optional.ofNullable(commentDto.getText()).ifPresent(comment::setText);
        Optional.of(item).ifPresent(comment::setItem);
        Optional.of(user).ifPresent(comment::setAuthor);
        return comment;
    }
}
