package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoFromRequest;
import ru.practicum.shareit.user.service.UserService;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void saveUserWhenAddAndThenStatusIsCreatedAndUserDtoReturned() {
        UserDtoFromRequest userDto1 = new UserDtoFromRequest("name1", "e1@email.com");
        long userId = 1L;
        String userName = userDto1.getName();
        String userEmail = userDto1.getEmail();

        UserDto userDtoAfterRequest = createUserDto(userId, userName, userEmail);
        when(userService.saveUser(userDto1))
                .thenReturn(userDtoAfterRequest);

        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto userFromJson = objectMapper.readValue(result, UserDto.class);
        assertEquals(userDtoAfterRequest, userFromJson);
        verify(userService, times(1)).saveUser(userDto1);
    }

    @SneakyThrows
    @Test
    void updateUserWhenUpdatedAndThenStatusIsOkAndUpdatedUserReturned() {
        UserDtoFromRequest userDtoFromRequest = new UserDtoFromRequest("new user", "new@email.com");
        long userId = 1L;
        String name = userDtoFromRequest.getName();
        String email = userDtoFromRequest.getEmail();
        UserDto userAfterRequest = createUserDto(userId, name, email);

        when(userService.updateUser(userDtoFromRequest, userId))
                .thenReturn(userAfterRequest);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(userDtoFromRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto userFromJson = objectMapper.readValue(result, UserDto.class);
        assertEquals(userAfterRequest, userFromJson);

        verify(userService, times(1)).updateUser(userDtoFromRequest, userId);
    }

    @SneakyThrows
    @Test
    void getAllUsersWhenGetTwoUsersAndThenStatusIsOkAndReturnedListOfUsersFromJson() {
        List<UserDto> users = List.of(new UserDto(1L, "3", "23@gmail.cof"), new UserDto(2L, "name", "fdsa@email.com"));

        when(userService.getAllUsers(any(Pageable.class)))
                .thenReturn(users);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)))
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> usersFromJson = objectMapper.readValue(result, new TypeReference<>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });

        assertEquals(users, usersFromJson);
        verify(userService, times(1)).getAllUsers(MyPageRequest.of(0, 20));
    }

    @SneakyThrows
    @Test
    void getUserByIdTestWhenGetUserAndThenStatusIsOkAndReturnedUserByIdFromJson() {
        long userId = 1L;
        UserDto userDto = new UserDto(userId, "name", "email@email.com");

        when(userService.getUserById(userId))
                .thenReturn(userDto);

        String result = mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto userDtoFromJson = objectMapper.readValue(result, UserDto.class);
        assertEquals(userDto, userDtoFromJson);

        verify(userService, times(1)).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void deleteUserTestWhenDeletedAndThenStatusIsOk() {
        long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }

    private UserDto createUserDto(long userId, String userName, String userEmail) {
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName(userName);
        userDto.setEmail(userEmail);

        return userDto;
    }
}