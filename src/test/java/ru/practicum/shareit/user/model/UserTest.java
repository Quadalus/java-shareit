package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    public void twoUserMustBeEquals() {
        User user = new User(1L, "name", "qwer@email.com");
        User user1 = new User(1L, "name", "qwer@email.com");

        assertEquals(user, user1);
        assertEquals(user, user);
        assertEquals(user.hashCode(), user1.hashCode());
    }
}