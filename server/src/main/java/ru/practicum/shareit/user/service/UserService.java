package ru.practicum.shareit.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoFromRequest;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers(Pageable pageable);

    UserDto getUserById(Long userId);

    UserDto saveUser(UserDtoFromRequest userDto);

    UserDto updateUser(UserDtoFromRequest userDto, Long userId);

    void deleteUser(Long userId);
}
