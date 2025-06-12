package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateBooking() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                1L);
        String bookingJson = objectMapper.writeValueAsString(bookingCreateDto);
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true,
                1L, null);
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemDto, userDto, BookingStatus.WAITING);
        when(bookingService.createBooking(1L, bookingCreateDto)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON).content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("test1"))
                .andExpect(jsonPath("$.booker.id").value(1));
        verify(bookingService, times(1)).createBooking(1L, bookingCreateDto);
    }

    @Test
    void testApprovedBooking() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true,
                1L, null);
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemDto, userDto, BookingStatus.APPROVED);
        when(bookingService.approvedBooking(1L, 1L, true)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("test1"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("Test1"));
        verify(bookingService, times(1)).approvedBooking(1L, 1L, true);
    }

    @Test
    void testGetBookingInformation() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true,
                1L, null);
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemDto, userDto, BookingStatus.APPROVED);
        when(bookingService.getBookingInformation(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("test1"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("Test1"));
        verify(bookingService, times(1)).getBookingInformation(1L, 1L);
    }

    @Test
    void testGetUserBookings() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true,
                1L, null);
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemDto, userDto, BookingStatus.APPROVED);
        when(bookingService.getUserBookings(1L, BookingSortStatus.ALL)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings?sort=ALL").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("test1"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("Test1"));
        verify(bookingService, times(1)).getUserBookings(1L, BookingSortStatus.ALL);
    }

    @Test
    void testGetOwnerItemsBooking() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true,
                1L, null);
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemDto, userDto, BookingStatus.APPROVED);
        when(bookingService.getOwnerItemsBooking(1L, BookingSortStatus.ALL)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("test1"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("Test1"));
        verify(bookingService, times(1)).getOwnerItemsBooking(1L, BookingSortStatus.ALL);
    }
}
