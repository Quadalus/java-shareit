package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDtoFromRequest itemDto, Long ownerId);

    ItemDto updateItem(ItemDtoFromRequest itemDto, Long itemId, Long ownerId);

    void deleteItem(Long itemId);

    ItemDetailedDto getItemById(Long itemId, Long userId);

    List<ItemDetailedDto> getUserItemsById(Pageable pageable, Long ownerId);

    List<ItemDto> getUserItemByText(Pageable pageable, Long ownerId, String text);

    CommentDto addCommentToItem(Long ownerId, Long itemId, CommentDtoFromRequest commentDto);
}
