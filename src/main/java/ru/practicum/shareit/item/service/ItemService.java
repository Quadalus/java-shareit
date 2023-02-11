package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFromRequest;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDtoFromRequest itemDto, Long ownerId);

    ItemDto updateItem(ItemDtoFromRequest itemDto, Long itemId, Long ownerId);

    void deleteItem(Long itemId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getUserItemsById(Long ownerId);

    List<ItemDto> getUserItemByText(Long ownerId, String text);
}
