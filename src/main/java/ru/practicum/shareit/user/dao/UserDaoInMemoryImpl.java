package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserDaoInMemoryImpl implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private static Long USER_ID_COUNT = 1L;

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User save(User user) {
        user.setId(USER_ID_COUNT);
        users.put(USER_ID_COUNT++, user);
        return user;
    }

    @Override
    public User update(User user, Long userId) {
        return users.put(userId, user);
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }
}
