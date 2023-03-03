package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(ItemRequestDtoFromRequest itemRequestDto, Long userId);

    List<ItemRequestDto> getUserRequest(Long userId);

    ItemRequestDto getRequestById(Long requestId, Long userId);

    List<ItemRequestDto> getAllRequests(Pageable pageable, Long userId);
}
