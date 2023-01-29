package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotNull;

public class ItemDtoMapper {
    public static ItemDto toItemDto(@NotNull Item item) {
        return new ItemDto.ItemDtoBuilder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItemFromDto(@NotNull ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable());
    }
}
