package ru.practicum.shareit.item.dao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemDaoInMemory implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private static Long ITEM_ID_COUNT = 1L;

    @Override
    public Item save(Item item) {
        item.setId(ITEM_ID_COUNT);
        items.put(ITEM_ID_COUNT++, item);
        return item;
    }

    @Override
    public Item update(Item item, Long itemId) {
        return items.put(itemId, item);
    }

    @Override
    public void delete(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> findAllItemsByUserId(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findUserItemsByText(Long userId, String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return items.values()
                .stream()
                .filter(item -> item.getAvailable().equals(Boolean.TRUE)
                        && (StringUtils.containsAnyIgnoreCase(item.getName(), text)
                        || StringUtils.containsAnyIgnoreCase(item.getDescription(), text)))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }
}
