package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookingItemDto item;
    private BookingUserDto user;

    @Getter
    @Setter
    @Builder
    public static class BookingItemDto {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    public static class BookingUserDto {
        private Long id;
    }
}
