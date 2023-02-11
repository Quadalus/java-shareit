package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDtoFromRequest itemDto, Long ownerId);

    ItemDto updateItem(ItemDtoFromRequest itemDto, Long itemId, Long ownerId);

    void deleteItem(Long itemId);

    ItemDetailedDto getItemById(Long itemId, Long userId);

    List<ItemDetailedDto> getUserItemsById(Long ownerId);

    List<ItemDto> getUserItemByText(Long ownerId, String text);

    CommentDto addCommentToItem(Long ownerId, Long itemId, CommentDtoFromRequest commentDto);
}
