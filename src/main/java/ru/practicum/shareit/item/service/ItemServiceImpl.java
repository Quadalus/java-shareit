package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.NoValidUserToCommentException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

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
    public ItemDetailedDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
        if (isOwner(userId, item)) {
            return getItemDetailedDto(item);
        }
        return getItemDetailedDtoWithoutBookings(item);
    }

    private static boolean isOwner(Long userId, Item item) {
        return item.getOwner().getId().equals(userId);
    }

    @Override
    public List<ItemDetailedDto> getUserItemsById(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        return items
                .stream()
                .map(this::getItemDetailedDto)
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

    @Transactional
    @Override
    public CommentDto addCommentToItem(Long ownerId, Long itemId, CommentDtoFromRequest commentDto) {
        checkUserToValidBookingItem(ownerId, itemId);
        User user = userRepository.findById(ownerId)
                .orElseThrow();
        Item item = itemRepository.findById(itemId)
                .orElseThrow();
        Comment comment = commentRepository.save(CommentDtoMapper.toComment(commentDto, item, user));
        return CommentDtoMapper.toCommentDto(comment);
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
        if (!isOwner(ownerId, item)) {
            throw new IncorrectParameterException("this user doesn't have this item");
        }
    }

    private BookingShortDto getBookingLast(Item item) {
        return bookingRepository.findFirstByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(), LocalDateTime.now())
                .stream()
                .map(BookingDtoMapper::toBookingShortDto)
                .findFirst()
                .orElse(null);
    }

    private BookingShortDto getBookingNext(Item item) {
        return bookingRepository.findFirstByItemIdAndStartIsAfter(item.getId(), LocalDateTime.now())
                .stream()
                .map(BookingDtoMapper::toBookingShortDto)
                .findFirst()
                .orElse(null);
    }

    private List<CommentDto> getItemsComments(Item item) {
        return commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId())
                .stream()
                .map(CommentDtoMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private void checkUserToValidBookingItem(Long userId, Long itemId) {
        LocalDateTime time = LocalDateTime.now();
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(userId, itemId, time)) {
            throw new NoValidUserToCommentException(String.format("this user with id=%d does not have access to " +
                    "comment this item with id=%d", userId, itemId));
        }
    }

    private ItemDetailedDto getItemDetailedDto(Item item) {
        List<CommentDto> comments = getItemsComments(item);
        BookingShortDto lastBooking = getBookingLast(item);
        BookingShortDto nextBooking = getBookingNext(item);
        return ItemDtoMapper.toItemDetailedDto(item, lastBooking, nextBooking, comments);
    }

    private ItemDetailedDto getItemDetailedDtoWithoutBookings(Item item) {
        List<CommentDto> comments = getItemsComments(item);
        return ItemDtoMapper.toItemDetailedDto(item, comments);
    }
}
