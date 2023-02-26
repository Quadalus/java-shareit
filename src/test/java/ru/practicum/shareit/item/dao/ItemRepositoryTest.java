package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    private long autoIncrementItemId = 1L;
    private long autoIncrementUserId = 1L;

    @AfterEach
    public void tearDown() {
        itemRepository.deleteAll();
    }

    @Test
    void findItemsByText() {
        User user = new User(autoIncrementUserId++, "name", "e@email.com");
        Item item = new Item(autoIncrementItemId++, "name", "desc", Boolean.TRUE, user, null);
        Item savedItem = itemRepository.save(item);

        List<Item> items = itemRepository
                .findItemsByText(MyPageRequest.of(0, 10), "name");

        assertEquals(items.size(), 1);
        assertEquals(savedItem, items.get(0));
    }

    @Test
    void findAllByOwnerId() {
        User user = new User(autoIncrementUserId++, "name", "e@email.com");
        Item item = new Item(autoIncrementItemId++, "name", "desc", Boolean.TRUE, user, null);
        Item savedItem = itemRepository.save(item);

        List<Item> items = itemRepository
                .findAllByOwnerId(MyPageRequest.of(0, 10), savedItem.getOwner().getId());

        assertEquals(items.size(), 1);
        assertEquals(savedItem, items.get(0));
    }
}