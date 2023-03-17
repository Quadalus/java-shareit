package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoFromRequest;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    @SneakyThrows
    void saveUserWhenAddAndThenStatusIsBadRequestAndUserNameIsWrong() {
        UserDtoFromRequest userDto1 = new UserDtoFromRequest("", "e1@email.com");
        long userId = 1L;
        String userName = userDto1.getName();
        String userEmail = userDto1.getEmail();

        UserDto userDtoAfterRequest = createUserDto(userId, userName, userEmail);

        when(userClient.saveUser(userDto1))
                .thenReturn(userDtoAfterRequest);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).saveUser(userDto1);
    }

    @Test
    @SneakyThrows
    void saveUserWhenAddAndThenStatusIsBadRequestAndUserEmailIsWrong() {
        UserDtoFromRequest userDto1 = new UserDtoFromRequest("name", "e1email.com");
        long userId = 1L;
        String userName = userDto1.getName();
        String userEmail = userDto1.getEmail();

        UserDto userDtoAfterRequest = createUserDto(userId, userName, userEmail);

        when(userClient.saveUser(userDto1))
                .thenReturn(userDtoAfterRequest);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).saveUser(userDto1);
    }

    private UserDto createUserDto(long userId, String userName, String userEmail) {
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName(userName);
        userDto.setEmail(userEmail);

        return userDto;
    }
}