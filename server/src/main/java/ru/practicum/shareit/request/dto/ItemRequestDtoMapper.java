package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public class ItemRequestDtoMapper {
    public static ItemRequestDto toDto(@NotNull ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDto toDto(@NotNull ItemRequest itemRequest, @NotNull List<ItemDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequest toItemRequest(@NotNull ItemRequestDtoFromRequest itemRequestDto, @NotNull User user) {
        ItemRequest itemRequest = new ItemRequest();
        Optional.ofNullable(user).ifPresent(itemRequest::setRequester);
        Optional.ofNullable(itemRequestDto.getDescription()).ifPresent(itemRequest::setDescription);
        return itemRequest;
    }
}
