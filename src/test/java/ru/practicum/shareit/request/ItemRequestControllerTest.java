package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private final String userHeader = "X-Sharer-User-Id";

    @SneakyThrows
    @Test
    void addRequestWhenPositiveCaseAndThenStatusIsOkAndReturnedItemRequestDtoFrom() {
        Long userId = 1L;
        Long itemRequestId = 1L;
        ItemRequestDtoFromRequest itemRequestDtoFromRequest = new ItemRequestDtoFromRequest("descriptions");
        ItemRequestDto itemRequestDto = getItemRequestDto(itemRequestDtoFromRequest, itemRequestId);
        itemRequestDto.setItems(Collections.emptyList());

        when(itemRequestService.addRequest(itemRequestDtoFromRequest, userId))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId)
                        .content(objectMapper.writeValueAsString(itemRequestDtoFromRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("descriptions"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemRequestDto itemRequestDtoFromJson = objectMapper.readValue(result, ItemRequestDto.class);
        assertEquals(itemRequestDto, itemRequestDtoFromJson);
    }

    @Test
    @SneakyThrows
    void getUserItemRequestWhenPositiveCaseAndThenStatusIsOkAndReturnedListOfItemRequestDto() {
        Long userId = 1L;
        Long itemRequestId = 1L;

        User user = getUser(userId);
        ItemRequest itemRequest = new ItemRequest(itemRequestId, "description", user, LocalDateTime.now());
        Item item = getItem(user, itemRequest);
        ItemDto itemDto = ItemDtoMapper.toItemDto(item);
        ItemRequestDto itemRequestDto = ItemRequestDtoMapper.toDto(itemRequest, List.of(itemDto));
        List<ItemRequestDto> itemRequests = List.of(itemRequestDto);

        when(itemRequestService.getUserRequest(userId))
                .thenReturn(itemRequests);

        String result = mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ItemRequestDto> itemRequestDtoFromJson = objectMapper.readValue(result, new TypeReference<List<ItemRequestDto>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        assertEquals(1, itemRequestDtoFromJson.size());
        assertEquals(itemRequestDto, itemRequestDtoFromJson.get(0));
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsWhenPositiveCaseAndThenStatusIsOkAndReturnedListOfItemRequestDto() {
        Long userId = 1L;
        Long itemRequestId = 1L;

        User user = getUser(userId);
        ItemRequest itemRequest = new ItemRequest(itemRequestId, "description", user, LocalDateTime.now());
        Item item = getItem(user, itemRequest);
        ItemDto itemDto = ItemDtoMapper.toItemDto(item);
        ItemRequestDto itemRequestDto = ItemRequestDtoMapper.toDto(itemRequest, List.of(itemDto));
        List<ItemRequestDto> itemRequests = List.of(itemRequestDto);

        when(itemRequestService.getAllRequests(MyPageRequest.of(0, 10), userId))
                .thenReturn(itemRequests);

        String result = mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ItemRequestDto> itemRequestDtoFromJson = objectMapper.readValue(result, new TypeReference<List<ItemRequestDto>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        assertEquals(1, itemRequestDtoFromJson.size());
        assertEquals(itemRequestDto, itemRequestDtoFromJson.get(0));
    }

    @SneakyThrows
    @Test
    void getItemRequestByIdWhenPositiveCaseAndThenStatusIsOkAndReturnedItemRequestDto() {
        Long userId = 1L;
        Long itemRequestId = 1L;

        User user = getUser(userId);
        ItemRequest itemRequest = new ItemRequest(itemRequestId, "description", user, LocalDateTime.now());
        Item item = getItem(user, itemRequest);
        ItemDto itemDto = ItemDtoMapper.toItemDto(item);
        ItemRequestDto itemRequestDto = ItemRequestDtoMapper.toDto(itemRequest, List.of(itemDto));

        when(itemRequestService.getRequestById(itemRequestId, userId))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(get("/requests/{requestId}", itemRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemRequestDto itemRequestDtoFromJson = objectMapper.readValue(result, ItemRequestDto.class);
        assertEquals(itemRequestDto, itemRequestDtoFromJson);
    }

    private ItemRequestDto getItemRequestDto(ItemRequestDtoFromRequest itemRequestDtoFromRequest, Long itemRequestId) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequestId);
        itemRequestDto.setDescription(itemRequestDtoFromRequest.getDescription());
        itemRequestDto.setCreated(LocalDateTime.now());

        return itemRequestDto;
    }

    private Item getItem(User user, ItemRequest itemRequest) {
        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setOwner(user);
        item.setDescription("description");
        item.setIsAvailable(Boolean.TRUE);
        item.setRequest(itemRequest);

        return item;
    }

    private User getUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setName("user name");
        user.setEmail("user@email.com");

        return user;
    }
}