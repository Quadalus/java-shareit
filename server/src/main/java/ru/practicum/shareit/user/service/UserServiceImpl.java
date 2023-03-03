package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoFromRequest;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id=%d not found", userId)));
        return UserDtoMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDtoFromRequest userDto) {
        User user = UserDtoMapper.toUserFromDto(userDto);
        User savedUser = userRepository.save(user);
        return UserDtoMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDtoFromRequest userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id=%d not found", userId)));
        User updatedUser = userRepository.save(UserDtoMapper.toUserFromDto(userDto, user));
        return UserDtoMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}

