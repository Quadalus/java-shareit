package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item save(Item item);

    Item update(Item item, Long itemId);

    void delete(Long itemId);

    List<Item> findAllItemsByUserId(Long userId);

    List<Item> findUserItemsByText(Long userId, String text);

    Optional<Item> findItemById(Long itemId);
}
