package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> findAllUsers();

    Optional<User> findUserById(Long userId);

    User save(User user);

    User update(User user, Long userId);

    void delete(Long userId);
}
