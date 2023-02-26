package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoFromRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void getAllUsersTestWhenInvokedAndThanReturnListOfUser() {
        User user = new User(1L, "name", "e@email.com");
        PageImpl<User> users = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(users);

        List<UserDto> usersFromService = userService.getAllUsers(MyPageRequest.of(0, 20));
        assertEquals(1, usersFromService.size());

        UserDto userFromList = usersFromService.get(0);
        assertEquals(user.getId(), userFromList.getId());
        assertEquals(user.getName(), userFromList.getName());
        assertEquals(user.getEmail(), userFromList.getEmail());
    }

    @Test
    void getUserByIdTestWhenUserFoundAndReturnUser() {
        long userId = 1L;
        User user = new User(userId, "name", "e@email.com");
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(userId);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByIdTestWhenUserNotFoundAndThrowNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void saveUserTest() {
        long userId = 1L;
        String name = "name";
        String email = "e@email.com";
        User user = new User(userId, name, email);
        UserDtoFromRequest savedDto = new UserDtoFromRequest();

        savedDto.setName(name);
        savedDto.setEmail(email);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto userDto = userService.saveUser(savedDto);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserWhenPositiveCaseAndThenReturnedUpdatedUser() {
        long userId = 1L;
        String name = "name";
        String email = "e@email.com";
        User user = new User(userId, name, email);
        UserDtoFromRequest updatedDto = new UserDtoFromRequest();

        updatedDto.setName(name);
        updatedDto.setEmail(email);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto userDto = userService.updateUser(updatedDto, userId);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUserWhenUserNotFoundAndThenThrowNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}