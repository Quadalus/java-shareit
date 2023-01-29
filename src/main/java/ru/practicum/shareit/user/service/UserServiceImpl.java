package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.findAllUsers()
                .stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userDao.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id=%d not found", userId)));
        return UserDtoMapper.toUserDto(user);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        checkEmailForUnique(userDto.getEmail());
        User user = UserDtoMapper.toUserFromDto(userDto);
        User savedUser = userDao.save(user);
        return UserDtoMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        checkEmailForUnique(userDto.getEmail());
        User user = userDao.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id=%d not found", userId)));
        User updatedUser = userDao.update(fillUserFields(user, userDto), userId);
        return UserDtoMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userDao.delete(userId);
    }

    private User fillUserFields(User user, UserDto userDto) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }

    private void checkEmailForUnique(String email) {
         boolean isUniqueEmail = userDao.findAllUsers()
                .stream()
                .map(User::getEmail)
                .noneMatch(userEmail -> userEmail.equals(email));

         if (!isUniqueEmail) {
             throw new EmailExistsException("this email already exists");
         }
    }
}
