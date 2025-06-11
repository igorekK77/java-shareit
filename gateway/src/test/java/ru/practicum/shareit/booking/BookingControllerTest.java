package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateBooking() throws Exception {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(startTime, endTime, 1L);
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true, 1L);
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");
        BookingDto bookingDto = new BookingDto(1L, startTime, endTime,
                itemDto, userDto, BookingStatus.WAITING);
        String bookingJson = objectMapper.writeValueAsString(bookingDto);
        ResponseEntity<Object> booking = new ResponseEntity<>(bookingJson, HttpStatus.OK);
        when(bookingClient.createBooking(eq(1L), any(BookingCreateDto.class)))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("test1"))
                .andExpect(jsonPath("$.booker.id").value(1));
        verify(bookingClient, times(1)).createBooking(eq(1L), any(BookingCreateDto.class));
    }

    @Test
    void testApprovedBooking() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true, 1L);
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemDto, userDto, BookingStatus.APPROVED);

        ResponseEntity<Object> booking = new ResponseEntity<>(bookingDto, HttpStatus.OK);
        when(bookingClient.approvedBooking(1L, 1L, true)).thenReturn(booking);

        mockMvc.perform(patch("/bookings/1?approved=true").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("test1"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("Test1"));
        verify(bookingClient, times(1)).approvedBooking(1L, 1L, true);
    }

    @Test
    void testGetBookingInformation() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true, 1L);
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemDto, userDto, BookingStatus.APPROVED);
        ResponseEntity<Object> booking = new ResponseEntity<>(bookingDto, HttpStatus.OK);
        when(bookingClient.getBookingInformation(1L, 1L)).thenReturn(booking);

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("test1"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("Test1"));
        verify(bookingClient, times(1)).getBookingInformation(1L, 1L);
    }

    @Test
    void testGetUserBookings() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true, 1L);
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemDto, userDto, BookingStatus.APPROVED);
        ResponseEntity<Object> booking = new ResponseEntity<>(List.of(bookingDto), HttpStatus.OK);
        when(bookingClient.getUserBookings(1L, "ALL")).thenReturn(booking);

        mockMvc.perform(get("/bookings?sort=ALL").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("test1"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("Test1"));
        verify(bookingClient, times(1)).getUserBookings(1L, "ALL");
    }

    @Test
    void testGetOwnerItemsBooking() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true, 1L);
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                itemDto, userDto, BookingStatus.APPROVED);
        ResponseEntity<Object> booking = new ResponseEntity<>(List.of(bookingDto), HttpStatus.OK);
        when(bookingClient.getOwnerItemsBooking(1L, "ALL")).thenReturn(booking);

        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("test1"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].booker.name").value("Test1"));
        verify(bookingClient, times(1)).getOwnerItemsBooking(1L, "ALL");
    }
}
