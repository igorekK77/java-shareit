package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestClientTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private RequestClient requestClient;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        requestClient = new RequestClient("http://localhost:9090", restTemplateBuilder);
    }

    @Test
    void createRequest() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        ItemRequestDescriptionDto itemRequestDescriptionDto = new ItemRequestDescriptionDto("test description");
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> request = requestClient.createRequest(1L, itemRequestDescriptionDto);
        Assertions.assertEquals(HttpStatus.OK, request.getStatusCode());
    }

    @Test
    void testGetRequestUser() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);
        ResponseEntity<Object> request = requestClient.getRequestUser(1L);
        Assertions.assertEquals(HttpStatus.OK, request.getStatusCode());
    }

    @Test
    void testGetAllRequests() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);
        ResponseEntity<Object> request = requestClient.getAllRequests(1L);
        Assertions.assertEquals(HttpStatus.OK, request.getStatusCode());
    }

    @Test
    void testGetRequest() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);
        ResponseEntity<Object> request = requestClient.getRequest(1L, 1L);
        Assertions.assertEquals(HttpStatus.OK, request.getStatusCode());
    }
}
