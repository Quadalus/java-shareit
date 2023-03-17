package ru.practicum.shareit.request.dto;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public class ItemRequestDtoMapper {
    public static ItemRequestDto toDto(@NonNull ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDto toDto(@NonNull ItemRequest itemRequest, @NonNull List<ItemDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequest toItemRequest(@NonNull ItemRequestDtoFromRequest itemRequestDto, @NonNull User user) {
        ItemRequest itemRequest = new ItemRequest();
        Optional.ofNullable(user).ifPresent(itemRequest::setRequester);
        Optional.ofNullable(itemRequestDto.getDescription()).ifPresent(itemRequest::setDescription);
        return itemRequest;
    }
}
