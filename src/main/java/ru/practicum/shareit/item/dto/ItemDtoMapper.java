package ru.practicum.shareit.item.dto;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public class ItemDtoMapper {
    public static ItemDto toItemDto(@NonNull Item item) {
        Optional<ItemRequest> itemRequest = Optional.ofNullable(item.getRequest());
        Long requestId = null;

        if (itemRequest.isPresent()) {
            requestId = itemRequest.get().getId();
        }
        return new ItemDto.ItemDtoBuilder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .requestId(requestId)
                .build();
    }

    public static ItemDetailedDto toItemDetailedDto(@NonNull Item item, @NonNull BookingShortDto last,
                                                    @NonNull BookingShortDto next, @NonNull List<CommentDto> comments) {
        return new ItemDetailedDto.ItemDetailedDtoBuilder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments)
                .build();
    }

    public static ItemDetailedDto toItemDetailedDto(@NonNull Item item, @NonNull List<CommentDto> comments) {
        return new ItemDetailedDto.ItemDetailedDtoBuilder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .comments(comments)
                .build();
    }

    public static Item toItemFromDto(@NonNull ItemDtoFromRequest itemDto) {
        Item item = new Item();
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setIsAvailable);
        return item;
    }
}
