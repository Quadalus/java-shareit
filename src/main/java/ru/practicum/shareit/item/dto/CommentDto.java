package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime createdTime;
}
