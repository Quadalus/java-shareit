package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromRequest;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final String userHeader = "X-Sharer-User-Id";

    @Test
    @SneakyThrows
    void addBookingWhenPositiveCaseThenStatusIsCreatedAndReturnedBookingDto() {
        Long userId = 1L;
        long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(30);
        BookingDtoFromRequest bookingDtoBeforeRequest = new BookingDtoFromRequest(1L, start, end);
        BookingDto bookingDtoAfterRequest = createBookingDto(bookingId, start, end);

        when(bookingService.addBooking(userId, bookingDtoBeforeRequest))
                .thenReturn(bookingDtoAfterRequest);

        String result = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId)
                        .content(objectMapper.writeValueAsString(bookingDtoBeforeRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoAfterRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDto bookingDto = objectMapper.readValue(result, BookingDto.class);
        assertEquals(bookingDtoAfterRequest, bookingDto);
    }

    @SneakyThrows
    @Test
    void bookingConfirmationWhenPositiveCaseThenHttpStatusIsOkAndBookingStatusIsApproved() {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(30);
        BookingDto bookingDtoAfterRequest = createBookingDto(bookingId, start, end);
        bookingDtoAfterRequest.setStatus(BookingStatus.APPROVED);

        when(bookingService.bookingConfirmation(userId, bookingId, approved))
                .thenReturn(bookingDtoAfterRequest);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId)
                        .queryParam("approved", approved.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoAfterRequest)))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDto bookingDto = objectMapper.readValue(result, BookingDto.class);
        assertEquals(bookingDtoAfterRequest, bookingDto);
    }

    @SneakyThrows
    @Test
    void getBookingByIdTest() {
        Long userId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(30);

        Item item = new Item();
        item.setId(1L);
        item.setName("name");

        User booker = new User();
        booker.setId(userId);

        Booking booking = new Booking(bookingId, start, end, item, booker, BookingStatus.WAITING);
        BookingDto bookingDto = BookingDtoMapper.toBookingDto(booking);

        when(bookingService.getBookingById(userId, bookingId))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("name"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookingDto bookingDtoFromJson = objectMapper.readValue(result, BookingDto.class);
        assertEquals(bookingDto, bookingDtoFromJson);
    }

    @SneakyThrows
    @Test
    void getBookingsByBookerTest() {
        Long userId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(30);

        Item item = new Item();
        item.setId(1L);
        item.setName("name");

        User booker = new User();
        booker.setId(userId);

        Booking booking = new Booking(bookingId, start, end, item, booker, BookingStatus.WAITING);
        BookingDto bookingDto = BookingDtoMapper.toBookingDto(booking);
        BookingDto bookingDto2 = BookingDtoMapper.toBookingDto(booking);

        List<BookingDto> bookings = List.of(bookingDto, bookingDto2);
        when(bookingService.getBookingsByBooker(MyPageRequest.of(0, 20), userId, "ALL"))
                .thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("state", "ALL")
                        .header(userHeader, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)))
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<BookingDto> bookingDtoFromJson = objectMapper.readValue(result, new TypeReference<List<BookingDto>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        assertEquals(bookings, bookingDtoFromJson);
    }

    @SneakyThrows
    @Test
    void getBookingsByOwner() {
        Long userId = 1L;
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(30);

        Item item = new Item();
        item.setId(1L);
        item.setName("name");

        User booker = new User();
        booker.setId(userId);

        Booking booking = new Booking(bookingId, start, end, item, booker, BookingStatus.WAITING);
        BookingDto bookingDto = BookingDtoMapper.toBookingDto(booking);
        BookingDto bookingDto2 = BookingDtoMapper.toBookingDto(booking);

        List<BookingDto> bookings = List.of(bookingDto, bookingDto2);
        when(bookingService.getBookingsByOwner(MyPageRequest.of(0, 20), userId, "ALL"))
                .thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings/owner", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("state", "ALL")
                        .header(userHeader, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)))
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<BookingDto> bookingDtoFromJson = objectMapper.readValue(result, new TypeReference<List<BookingDto>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        assertEquals(bookings, bookingDtoFromJson);
    }

    private BookingDto createBookingDto(long bookingId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        return bookingDto;
    }
}