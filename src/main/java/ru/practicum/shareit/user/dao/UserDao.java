package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao {
    List<User> findAllUsers();

    Optional<User> findUserById(Long userId);

    User save(User user);

    User update(User user, Long userId);

    void delete(Long userId);
}
