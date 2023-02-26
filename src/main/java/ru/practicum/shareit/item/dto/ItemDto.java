package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return Objects.equals(id, itemDto.id) && Objects.equals(name, itemDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}


