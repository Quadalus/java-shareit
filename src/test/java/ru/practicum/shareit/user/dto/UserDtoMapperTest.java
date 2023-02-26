package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserDtoMapperTest {
    @Test
    void toUserDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setEmail("e@email.com");
        user.setName("name");

        UserDto userDto = UserDtoMapper.toUserDto(user);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void toUserFromDtoTest() {
        UserDtoFromRequest userDto = new UserDtoFromRequest("name", "e@email.com");

        User user = UserDtoMapper.toUserFromDto(userDto);

        assertNotNull(userDto);
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void testToUserFromDtoTest() {
        UserDtoFromRequest userDtoToUpdate = new UserDtoFromRequest("newName", "new@email.com");
        User user = new User();
        user.setId(1L);
        user.setEmail("e@email.com");
        user.setName("name");

        User updateUser = UserDtoMapper.toUserFromDto(userDtoToUpdate, user);

        assertNotNull(updateUser);
        assertEquals(1L, updateUser.getId());
        assertEquals(userDtoToUpdate.getName(), updateUser.getName());
        assertEquals(userDtoToUpdate.getEmail(), updateUser.getEmail());
    }
}