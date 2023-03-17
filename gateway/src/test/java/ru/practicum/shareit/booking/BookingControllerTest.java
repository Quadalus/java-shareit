package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    private final String userHeader = "X-Sharer-User-Id";

    @Test
    @SneakyThrows
    void addBookingWhenStartIsBeforeEndThenStatusIsBadRequest() {
        Long userId = 1L;
        long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(30);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingDtoFromRequest bookingDtoBeforeRequest = new BookingDtoFromRequest(1L, start, end);
        BookingDto bookingDtoAfterRequest = createBookingDto(bookingId, start, end);

        when(bookingClient.addBooking(userId, bookingDtoBeforeRequest))
                .thenReturn(bookingDtoAfterRequest);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId)
                        .content(objectMapper.writeValueAsString(bookingDtoBeforeRequest)))
                .andExpect(status().isBadRequest());
    }

    private BookingDto createBookingDto(long bookingId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        return bookingDto;
    }
}
