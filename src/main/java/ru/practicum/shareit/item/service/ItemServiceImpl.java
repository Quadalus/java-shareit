package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long ownerId) {
        checkValidOwner(ownerId);
        Item item = ItemDtoMapper.toItemFromDto(itemDto);
        item.setOwnerId(ownerId);
        Item savedItem = itemDao.save(item);
        return ItemDtoMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        checkValidOwner(ownerId);
        Item item = itemDao.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
        checkValidOwnerToItem(item, ownerId);
        Item updatedItem = itemDao.update(fillItemFields(item, itemDto), itemId);
        return ItemDtoMapper.toItemDto(updatedItem);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemDao.delete(itemId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemDao.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
        return ItemDtoMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItemsById(Long ownerId) {
        return itemDao.findAllItemsByUserId(ownerId)
                .stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getUserItemByText(Long ownerId, String text) {
        return itemDao.findUserItemsByText(ownerId, text)
                .stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item fillItemFields(Item item, ItemDto itemDto) {
        String itemName = itemDto.getName();
        String itemDescription = itemDto.getDescription();
        Boolean itemIsAvailable = itemDto.getAvailable();

        Optional.ofNullable(itemName).ifPresent(item1 -> item.setName(itemName));
        Optional.ofNullable(itemDescription).ifPresent(item1 -> item.setDescription(itemDescription));
        Optional.ofNullable(itemIsAvailable).ifPresent(item1 -> item.setAvailable(itemIsAvailable));
        return item;
    }

    private void checkValidOwner(Long ownerId) {
        if (userDao.findUserById(ownerId).isEmpty()) {
            throw new NotFoundException(String.format("user with id=%d not found", ownerId));
        }
    }

    private void checkValidOwnerToItem(Item item, Long ownerId) {
        if (!item.getOwnerId().equals(ownerId)) {
            throw new IncorrectParameterException("this owner doesn't have this item");
        }
    }
}
