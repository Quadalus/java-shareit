package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestTest {
    @Test
    public void twoItemRequestsMustBeEquals() {
        User user = new User(1L, "name", "w@email.com");

        ItemRequest itemRequest = new ItemRequest(1L, "desc", user, LocalDateTime.now());
        ItemRequest itemRequest1 = new ItemRequest(1L, "desc", user, LocalDateTime.now());

        assertEquals(itemRequest, itemRequest1);
        assertEquals(itemRequest, itemRequest1);
        assertEquals(itemRequest.hashCode(), itemRequest1.hashCode());
    }
}