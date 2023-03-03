package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {
    @Test
    public void twoItemMustBeEquals() {
        User user = new User(1L, "name", "e@email.com");
        Item item = new Item(1L, "name", "desc", Boolean.TRUE, user, null);
        Item item1 = new Item(1L, "name", "desc", Boolean.TRUE, user, null);

        assertEquals(item, item);
        assertEquals(item, item1);
        assertEquals(item.hashCode(), item1.hashCode());
    }
}