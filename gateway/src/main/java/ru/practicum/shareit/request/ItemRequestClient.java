package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.request.dto.ItemRequestDtoFromRequest;
import ru.practicum.shareit.request.dto.ItemRequetsDto;

import java.util.List;

@Service
public class ItemRequestClient {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final WebClient client;

    public ItemRequestClient(@Value("${shareit-server.url}") String url) {
        this.client = WebClient.create(url);
    }

    public ItemRequetsDto getRequestById(Long requestId, Long userId) {
        return client
                .get()
                .uri("/requests/{requestId}", requestId)
                .header(USER_HEADER, userId.toString())
                .retrieve()
                .bodyToMono(ItemRequetsDto.class)
                .block();
    }

    public List<ItemRequetsDto> getUserRequest(Long userId) {
        return client.get()
                .uri("/requests")
                .header(USER_HEADER, userId.toString())
                .retrieve()
                .bodyToFlux(ItemRequetsDto.class)
                .collectList()
                .block();
    }

    public List<ItemRequetsDto> getAllRequests(Integer from, Integer size, Long userId) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/requests/all")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(USER_HEADER, userId.toString())
                .retrieve()
                .bodyToFlux(ItemRequetsDto.class)
                .collectList()
                .block();
    }

    public ItemRequetsDto addRequest(ItemRequestDtoFromRequest itemRequestDto, Long userId) {
        return client.post()
                .uri("/requests")
                .header(USER_HEADER, userId.toString())
                .bodyValue(itemRequestDto)
                .retrieve()
                .bodyToMono(ItemRequetsDto.class)
                .block();
    }
}
