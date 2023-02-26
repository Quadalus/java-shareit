package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.item.exception.NoValidUserToCommentException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
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
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto saveItem(ItemDtoFromRequest itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id=%d not found", ownerId)));
        Item item = ItemDtoMapper.toItemFromDto(itemDto);
        setItemRequestIfExists(itemDto, item);
        item.setOwner(owner);
        itemRepository.save(item);
        return ItemDtoMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDtoFromRequest itemDto, Long itemId, Long ownerId) {
        checkValidOwner(ownerId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
        checkValidOwnerToItem(item, ownerId);
        Item itemToSave = fillItemFields(item, itemDto);
        itemRepository.save(itemToSave);
        return ItemDtoMapper.toItemDto(itemToSave);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
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
    public List<ItemDetailedDto> getUserItemsById(Pageable pageable, Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(pageable, ownerId);
        return items
                .stream()
                .map(this::getItemDetailedDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getUserItemByText(Pageable pageable, Long ownerId, String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }

        return itemRepository.findItemsByText(pageable, text)
                .stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addCommentToItem(Long ownerId, Long itemId, CommentDtoFromRequest commentDto) {
        checkUserToValidBookingItem(ownerId, itemId);
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id=%d not found", ownerId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));
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

    private void setItemRequestIfExists(ItemDtoFromRequest itemDtoFromRequest, Item item) {
        if (itemDtoFromRequest.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.getReferenceById(itemDtoFromRequest.getRequestId());
            item.setRequest(itemRequest);
        }
    }
}
