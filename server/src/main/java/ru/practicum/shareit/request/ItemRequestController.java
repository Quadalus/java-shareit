package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto addRequest(@RequestBody ItemRequestDtoFromRequest itemRequestDto,
                                     @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getUserItemRequest(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getUserRequest(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllItemRequests(@RequestParam(required = false, defaultValue = "0") int from,
                                                   @RequestParam(required = false, defaultValue = "10") int size,
                                                   @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getAllRequests(MyPageRequest.of(from, size), userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
