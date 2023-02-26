package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final Long userId = 1L;
    private User user = new User(userId, "name", "e@email.com");
    private final Long itemRequestId = 1L;

    @Test
    void addRequestWhenPositiveCaseAndThenSaveRequestAndReturnedItemRequestDto() {
        ItemRequestDtoFromRequest itemRequestDtoFromRequest = new ItemRequestDtoFromRequest("description");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        user = userRepository.findById(userId).get();
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDtoFromRequest, user);

        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        itemRequestRepository.save(itemRequest);
        ItemRequestDto itemRequestDto = itemRequestService.addRequest(itemRequestDtoFromRequest, userId);

        assertNotNull(itemRequestDto);
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());

        verify(userRepository, times(2)).findById(userId);
        verify(itemRequestRepository, times(1)).save(itemRequest);
    }

    @Test
    void addRequestWhenUserNotFoundThenThrowNotFoundException() {
        ItemRequestDtoFromRequest itemRequestDtoFromRequest = new ItemRequestDtoFromRequest("description");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDtoFromRequest, user);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.addRequest(itemRequestDtoFromRequest, userId));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(0)).save(itemRequest);
    }

    @Test
    void getUserRequestWhenPositiveCaseAndThenReturnedListOfItemRequestDto() {
        ItemRequestDtoFromRequest itemRequestDtoFromRequest = new ItemRequestDtoFromRequest("description");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDtoFromRequest, user);
        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(itemRequestRepository.findAllByRequesterId(userId)).thenReturn(itemRequests);
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getUserRequest(userId);

        assertEquals(itemRequestDtos.size(), 1);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserRequestWhenUserNotExistsAndThenThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getUserRequest(userId));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(0)).findAllByRequesterId(userId);
    }

    @Test
    void getRequestByIdWhenPositiveCaseAndThenReturnedItemRequestDto() {
        ItemRequestDtoFromRequest itemRequestDtoFromRequest = new ItemRequestDtoFromRequest("description");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDtoFromRequest, user);
        itemRequest.setId(1L);

        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        ItemRequestDto itemRequestDto = itemRequestService.getRequestById(itemRequestId, userId);

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getRequestByIdWhenUserNotExistsAndThenThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(itemRequestId, userId));

        assertEquals(NotFoundException.class, notFoundException.getClass());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(0)).findById(itemRequestId);
    }

    @Test
    void getRequestByIdWhenItemRequestNotExistsAndThenThrowItemRequestNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        user = userRepository.findById(userId).get();

        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());
        ItemRequestNotFoundException itemRequestNotFoundException = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getRequestById(itemRequestId, userId));

        assertEquals(ItemRequestNotFoundException.class, itemRequestNotFoundException.getClass());
        verify(userRepository, times(2)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(itemRequestId);
    }

    @Test
    void getAllRequestsWhenPositiveCaseAndReturnedListOfItemRequestDto() {
        ItemRequestDtoFromRequest itemRequestDtoFromRequest = new ItemRequestDtoFromRequest("description");

        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDtoFromRequest, user);
        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(itemRequestRepository.findAllByRequesterIdNot(MyPageRequest.of(0, 10), userId)).thenReturn(itemRequests);
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllRequests(MyPageRequest.of(0, 10), userId);

        assertEquals(itemRequestDtos.size(), itemRequests.size());
        assertEquals(itemRequestDtos.get(0).getId(), itemRequests.get(0).getId());
        assertEquals(itemRequestDtos.get(0).getDescription(), itemRequests.get(0).getDescription());
    }
}