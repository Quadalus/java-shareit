package ru.practicum.shareit.item.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ItemDtoFromRequest {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
