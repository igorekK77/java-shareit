package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingClientTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        bookingClient = new BookingClient("http://localhost:9090", restTemplateBuilder);
    }

    @Test
    void testCreateBooking() {
        BookingCreateDto bookingDto = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                1L);
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> booking = bookingClient.createBooking(1L, bookingDto);
        Assertions.assertEquals(HttpStatus.OK, booking.getStatusCode());
    }

    @Test
    void testApprovedBooking() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap()
        )).thenReturn(mockResponse);

        ResponseEntity<Object> booking = bookingClient.approvedBooking(1L, 1L, true);
        Assertions.assertEquals(HttpStatus.OK, booking.getStatusCode());
    }

    @Test
    void testGetBookingInformation() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> booking = bookingClient.getBookingInformation(1L, 1L);
        Assertions.assertEquals(HttpStatus.OK, booking.getStatusCode());
    }

    @Test
    void testGetUserBookings() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap()
        )).thenReturn(mockResponse);

        ResponseEntity<Object> booking = bookingClient.getUserBookings(1L, "ALL");
        Assertions.assertEquals(HttpStatus.OK, booking.getStatusCode());
    }

    @Test
    void testGetOwnerItemsBooking() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap()
        )).thenReturn(mockResponse);

        ResponseEntity<Object> booking = bookingClient.getOwnerItemsBooking(1L, "ALL");
        Assertions.assertEquals(HttpStatus.OK, booking.getStatusCode());
    }
}
