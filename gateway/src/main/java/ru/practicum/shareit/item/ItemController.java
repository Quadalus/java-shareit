package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto saveItem(@RequestHeader(USER_HEADER) Long ownerId,
                            @Valid @RequestBody ItemDtoFromRequest itemDto) {
        log.info("the item has been saved");
        return itemClient.saveItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader(USER_HEADER) Long ownerId,
                              @RequestBody ItemDtoFromRequest itemDto,
                              @PathVariable Long itemId) {
        log.info("the item with id={} has been saved", itemId);
        return itemClient.updateItem(itemDto, itemId, ownerId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long itemId) {
        log.info("the item with id={} has been deleted", itemId);
        itemClient.deleteItem(itemId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDetailedDto findItemById(@RequestHeader(USER_HEADER) Long userId,
                                        @PathVariable Long itemId) {
        log.info("the item with id={} has been got", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDetailedDto> getUserItemsById(@RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(required = false, defaultValue = "10") @Positive int size,
                                                  @RequestHeader(USER_HEADER) Long ownerId) {
        log.info("the user item's has been got");
        return itemClient.getUserItemsById(from, size, ownerId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getUserItemByText(@RequestHeader(USER_HEADER) Long ownerId,
                                           @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(required = false, defaultValue = "10") @Positive int size,
                                           @RequestParam String text) {
        log.info("the user item's has been got");
        return itemClient.getUserItemByText(from, size, ownerId, text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addCommentToItem(@RequestHeader(USER_HEADER) Long ownerId,
                                       @PathVariable Long itemId,
                                       @RequestBody @Valid CommentDtoFromRequest commentDto) {
        log.info("comment to item with id={} added", itemId);
        return itemClient.addCommentToItem(ownerId, itemId, commentDto);
    }
}
