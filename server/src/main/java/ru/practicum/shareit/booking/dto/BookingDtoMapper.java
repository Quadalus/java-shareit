package ru.practicum.shareit.booking.dto;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingDto.BookingUserDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingDtoMapper {
    public static BookingDto toBookingDto(@NonNull Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(toBookingItemDto(booking.getItem()))
                .booker(toBookingUserDto(booking.getBooker()))
                .build();
    }

    public static BookingShortDto toBookingShortDto(@NonNull Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking fromBookingDto(@NonNull BookingDtoFromRequest bookingDto, @NonNull User user,
                                         @NonNull Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        return booking;
    }

    private static BookingItemDto toBookingItemDto(@NonNull Item item) {
        return BookingItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    private static BookingUserDto toBookingUserDto(@NonNull User user) {
        return BookingUserDto.builder()
                .id(user.getId())
                .build();
    }
}
