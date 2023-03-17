package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String userHeader = "X-Sharer-User-Id";
    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final Long bookingLastId = 1L;
    private final Long bookingNextId = 2L;
    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now().plusDays(5);

    @SneakyThrows
    @Test
    void saveItemWhenPositiveCaseAndThenStatusIsCreatedAndReturnedItemDto() {
        ItemDtoFromRequest itemDtoFromRequest = new ItemDtoFromRequest("name", "description", Boolean.TRUE, null);
        ItemDto itemDto = createItemDto(itemDtoFromRequest);

        when(itemService.saveItem(itemDtoFromRequest, userId)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoFromRequest))
                        .header(userHeader, userId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(Boolean.TRUE))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDto itemDtoFromJson = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDto, itemDtoFromJson);
    }

    @SneakyThrows
    @Test
    void updateItemWhenPositiveCaseAndThenStatusIsOkAndReturnedItemDto() {
        ItemDtoFromRequest itemDtoFromRequest = new ItemDtoFromRequest("name", "description", Boolean.TRUE, null);
        ItemDto itemDto = createItemDto(itemDtoFromRequest);
        itemDto.setId(itemId);

        when(itemService.updateItem(itemDtoFromRequest, itemId, userId)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoFromRequest))
                        .header(userHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(Boolean.TRUE))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDto itemDtoFromJson = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDto, itemDtoFromJson);
    }

    @SneakyThrows
    @Test
    void deleteItemWhenPositiveCaseAndThenStatusIsNoContentAndDeleteItemAndReturnedNothing() {
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteItem(itemId);
    }

    @SneakyThrows
    @Test
    void findItemByIdWhenPositiveCaseAndThenStatusIsOkAndReturnedItemDetailedDto() {
        BookingShortDto bookingLast = createBookingShortDto(bookingLastId);
        BookingShortDto bookingNext = createBookingShortDto(bookingNextId);
        ItemDetailedDto itemDetailedDto = createItemDetailedDto(bookingLast, bookingNext);

        when(itemService.getItemById(itemId, userId)).thenReturn(itemDetailedDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDetailedDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDetailedDto itemDetailedDtoFromJson = objectMapper.readValue(result, ItemDetailedDto.class);
        assertEquals(itemDetailedDto, itemDetailedDtoFromJson);

        verify(itemService, times(1)).getItemById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getUserItemsByIdWhenPositiveCaseAndThenStatusIsOkAndReturnedListOfItemDetailedDto() {
        BookingShortDto bookingLast = createBookingShortDto(bookingLastId);
        BookingShortDto bookingNext = createBookingShortDto(bookingNextId);
        ItemDetailedDto itemDetailedDto = createItemDetailedDto(bookingLast, bookingNext);
        List<ItemDetailedDto> items = List.of(itemDetailedDto);

        when(itemService.getUserItemsById(MyPageRequest.of(0, 10), userId)).thenReturn(items);

        String result = mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)))
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ItemDetailedDto> itemsFromJson = objectMapper.readValue(result, new TypeReference<List<ItemDetailedDto>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });

        assertEquals(items.size(), itemsFromJson.size());
        assertEquals(items.get(0), itemsFromJson.get(0));
        verify(itemService, times(1)).getUserItemsById(MyPageRequest.of(0, 10), userId);
    }

    @SneakyThrows
    @Test
    void getUserItemByTextWhenPositiveCaseAndThenStatusIsOkAndReturnedListOfItemDto() {
        ItemDto itemDto = createItemDto();
        List<ItemDto> items = List.of(itemDto);
        String text = "description";

        when(itemService.getUserItemByText(MyPageRequest.of(0, 10), userId, text)).thenReturn(items);

        String result = mockMvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId)
                        .queryParam("text", text))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)))
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ItemDto> itemsFromJson = objectMapper.readValue(result, new TypeReference<List<ItemDto>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        assertEquals(items.size(), itemsFromJson.size());
        assertEquals(items.get(0), itemsFromJson.get(0));
        verify(itemService, times(1)).getUserItemByText(MyPageRequest.of(0, 10), userId, text);
    }

    @SneakyThrows
    @Test
    void addCommentToItemWhenPositiveCaseAndThenStatusIsOkAndReturnedCommentDto() {
        long commentId = 1L;
        String text = "text";
        CommentDtoFromRequest commentDtoFromRequest = new CommentDtoFromRequest(text);
        String authorName = "user name";
        CommentDto commentDto = new CommentDto(commentId, commentDtoFromRequest.getText(), authorName, start);

        when(itemService.addCommentToItem(userId, itemId, commentDtoFromRequest)).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId)
                        .content(objectMapper.writeValueAsString(commentDtoFromRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.authorName").value(authorName))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.createdTime").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CommentDto commentDtoFromJson = objectMapper.readValue(result, CommentDto.class);
        assertEquals(commentDto, commentDtoFromJson);

        verify(itemService, times(1)).addCommentToItem(userId, itemId, commentDtoFromRequest);
    }

    private ItemDto createItemDto(ItemDtoFromRequest itemDtoFromRequest) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(itemDtoFromRequest.getName());
        itemDto.setDescription(itemDtoFromRequest.getDescription());
        itemDto.setAvailable(itemDtoFromRequest.getAvailable());

        return itemDto;
    }

    private ItemDto createItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(Boolean.TRUE);

        return itemDto;
    }

    private BookingShortDto createBookingShortDto(Long bookingId) {
        BookingShortDto bookingShortDto = new BookingShortDto();
        bookingShortDto.setId(bookingId);
        bookingShortDto.setBookerId(userId);
        bookingShortDto.setStart(start);
        bookingShortDto.setEnd(end);

        return bookingShortDto;
    }

    private ItemDetailedDto createItemDetailedDto(BookingShortDto lastBooking, BookingShortDto nextBooking) {
        ItemDetailedDto itemDto = new ItemDetailedDto();
        itemDto.setId(itemId);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(Boolean.TRUE);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(Collections.emptyList());

        return itemDto;
    }
}