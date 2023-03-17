package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;

import java.util.List;

@Service
public class BookingClient {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final WebClient client;

    public BookingClient(@Value("${shareit-server.url}") String url) {
        this.client = WebClient.create(url);
    }

    public BookingDto addBooking(Long userId, BookingDtoFromRequest bookingDtoFromRequest) {
        return client.post()
                .uri("/bookings")
                .header(USER_HEADER, userId.toString())
                .bodyValue(bookingDtoFromRequest)
                .retrieve()
                .bodyToMono(BookingDto.class)
                .block();
    }

    public BookingDto bookingConfirmation(Long userId, Long bookingId, Boolean approved) {
        return client.patch()
                .uri(uriBuilder -> uriBuilder.path("/bookings/" + bookingId)
                        .queryParam("approved", approved.toString())
                        .build())
                .header(USER_HEADER, userId.toString())
                .retrieve()
                .bodyToMono(BookingDto.class)
                .block();
    }

    public BookingDto getBookingById(Long userId, Long bookingId) {
        return client
                .get()
                .uri("/bookings/{bookingId}", bookingId)
                .header(USER_HEADER, userId.toString())
                .retrieve()
                .bodyToMono(BookingDto.class)
                .block();
    }

    public List<BookingDto> getBookingsByBooker(Integer from, Integer size, Long userId, String state) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings")
                        .queryParam("state", state)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(USER_HEADER, userId.toString())
                .retrieve()
                .bodyToFlux(BookingDto.class)
                .collectList()
                .block();
    }

    public List<BookingDto> getBookingsByOwner(Integer from, Integer size, Long userId, String state) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/bookings/owner")
                        .queryParam("state", state)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(USER_HEADER, userId.toString())
                .retrieve()
                .bodyToFlux(BookingDto.class)
                .collectList()
                .block();
    }
}
