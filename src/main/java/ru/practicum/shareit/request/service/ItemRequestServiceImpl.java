package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addRequest(ItemRequestDtoFromRequest itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id=%d not found", userId)));
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDto, user);
        itemRequestRepository.save(itemRequest);
        return ItemRequestDtoMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequest(Long userId) {
        checkUserExists(userId);
        return itemRequestRepository.findAllByRequesterId(userId)
                .stream()
                .map(itemRequest -> ItemRequestDtoMapper.toDto(itemRequest, getItemsToItemRequest()))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        checkUserExists(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(String.format("itemRequest with id=%d not found",
                        requestId)));
        return ItemRequestDtoMapper.toDto(itemRequest, getItemsToItemRequest());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Pageable pageable, Long userId) {
        return itemRequestRepository.findAllByRequesterIdNot(pageable, userId)
                .stream()
                .map(itemRequest -> ItemRequestDtoMapper.toDto(itemRequest, getItemsToItemRequest()))
                .collect(Collectors.toList());
    }

    private void checkUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("user with id=%d not found", userId));
        }
    }

    private List<ItemDto> getItemsToItemRequest() {
        List<Item> items = itemRepository.findAll();
        List<Item> itemsToItemRequest = new ArrayList<>();

        for (Item item : items) {
            if (item.getRequest() != null) {
                itemsToItemRequest.add(item);
            }
        }

        return itemsToItemRequest.stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
