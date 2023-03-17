package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.item.dto.*;

import java.util.Comparator;
import java.util.List;

@Service
public class ItemClient {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final WebClient client;

    public ItemClient(@Value("${shareit-server.url}") String url) {
        this.client = WebClient.create(url);
    }

    public ItemDto saveItem(ItemDtoFromRequest itemDto, Long userId) {
        return client.post()
                .uri("/items")
                .header(USER_HEADER, userId.toString())
                .bodyValue(itemDto)
                .retrieve()
                .bodyToMono(ItemDto.class)
                .block();
    }

    public ItemDto updateItem(ItemDtoFromRequest itemDto, Long itemId, Long userId) {
        return client.patch()
                .uri("/items/{itemId}", itemId)
                .header(USER_HEADER, userId.toString())
                .bodyValue(itemDto)
                .retrieve()
                .bodyToMono(ItemDto.class)
                .block();
    }

    public void deleteItem(Long itemId) {
        client.delete()
                .uri("/items/{itemId}", itemId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public ItemDetailedDto getItemById(Long itemId, Long userId) {
        return client
                .get()
                .uri("/items/{itemId}", itemId)
                .header(USER_HEADER, userId.toString())
                .retrieve()
                .bodyToMono(ItemDetailedDto.class)
                .block();
    }

    public List<ItemDetailedDto> getUserItemsById(Integer from, Integer size, Long userId) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(USER_HEADER, userId.toString())
                .retrieve()
                .bodyToFlux(ItemDetailedDto.class)
                .sort(Comparator.comparingLong(ItemDetailedDto::getId))
                .collectList()
                .block();
    }

    public List<ItemDto> getUserItemByText(Integer from, Integer size, Long userId, String text) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/items/search")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .queryParam("text", text)
                        .build())
                .header(USER_HEADER, userId.toString())
                .retrieve()
                .bodyToFlux(ItemDto.class)
                .collectList()
                .block();
    }

    public CommentDto addCommentToItem(Long ownerId, Long itemId, CommentDtoFromRequest commentDto) {
        return client.post()
                .uri("/items/{itemId}/comment", itemId)
                .header(USER_HEADER, ownerId.toString())
                .bodyValue(commentDto)
                .retrieve()
                .bodyToMono(CommentDto.class)
                .block();
    }
}
