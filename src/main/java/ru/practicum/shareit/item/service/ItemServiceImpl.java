package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoFromRequest;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto saveItem(ItemDtoFromRequest itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        Item item = ItemDtoMapper.toItemFromDto(itemDto);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        return ItemDtoMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(ItemDtoFromRequest itemDto, Long itemId, Long ownerId) {
        checkValidOwner(ownerId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
        checkValidOwnerToItem(item, ownerId);
        Item updatedItem = itemRepository.save(fillItemFields(item, itemDto));
        return ItemDtoMapper.toItemDto(updatedItem);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItemById(itemId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
        return ItemDtoMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItemsById(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getUserItemByText(Long ownerId, String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }

        return itemRepository.findItemsByText(text)
                .stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item fillItemFields(Item item, ItemDtoFromRequest itemDto) {
        String itemName = itemDto.getName();
        String itemDescription = itemDto.getDescription();
        Boolean itemIsAvailable = itemDto.getAvailable();

        Optional.ofNullable(itemName).ifPresent(item::setName);
        Optional.ofNullable(itemDescription).ifPresent(item::setDescription);
        Optional.ofNullable(itemIsAvailable).ifPresent(item::setIsAvailable);
        return item;
    }

    private void checkValidOwner(Long ownerId) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException(String.format("user with id=%d not found", ownerId));
        }
    }

    private void checkValidOwnerToItem(Item item, Long ownerId) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new IncorrectParameterException("this owner doesn't have this item");
        }
    }
}
