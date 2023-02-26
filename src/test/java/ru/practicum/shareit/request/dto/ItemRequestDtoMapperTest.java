package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemRequestDtoMapperTest {
    private ItemRequest itemRequest;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "name", "e@email.com");
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now());
    }

    @AfterEach
    public void tearDown() {
        user = null;
        itemRequest = null;
    }

    @Test
    void toDtoTestFromOneArgument() {
        ItemRequestDto itemRequestDtoDto = ItemRequestDtoMapper.toDto(itemRequest);

        assertNotNull(itemRequestDtoDto);
        assertEquals(itemRequest.getId(), itemRequestDtoDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDtoDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDtoDto.getCreated());
    }

    @Test
    void toDtoTestFromTwoArgument() {
        ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE, null);
        List<ItemDto> items = List.of(itemDto);
        ItemRequestDto itemRequestDtoDto = ItemRequestDtoMapper.toDto(itemRequest, items);

        assertNotNull(itemRequestDtoDto);
        assertEquals(itemRequest.getId(), itemRequestDtoDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDtoDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDtoDto.getCreated());
        assertEquals(items, itemRequestDtoDto.getItems());
        assertEquals(items.size(), itemRequestDtoDto.getItems().size());
    }


    @Test
    void toItemRequestTest() {
        ItemRequestDtoFromRequest itemDtoFromRequest = new ItemRequestDtoFromRequest("description");
        ItemRequest itemRequestFromDto = ItemRequestDtoMapper.toItemRequest(itemDtoFromRequest, user);

        assertNotNull(itemRequestFromDto);
        assertEquals(itemRequest.getDescription(), itemRequestFromDto.getDescription());
        assertEquals(itemRequest.getRequester(), itemRequestFromDto.getRequester());
    }
}