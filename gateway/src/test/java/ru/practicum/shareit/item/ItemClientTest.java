package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.TextCommentDto;

import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemClientTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        itemClient = new ItemClient("http://localhost:9090", restTemplateBuilder);
    }

    @Test
    void testCreateItem() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", true,
                null);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> item = itemClient.createItem(1L, itemCreateDto);
        Assertions.assertEquals(HttpStatus.OK, item.getStatusCode());
    }

    @Test
    void testUpdateItem() {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true, 1L);
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> item = itemClient.updateItem(1L, 1L, itemDto);
        Assertions.assertEquals(HttpStatus.OK, item.getStatusCode());
    }

    @Test
    void testGetItemById() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> item = itemClient.getItemById(1L, 1L);
        Assertions.assertEquals(HttpStatus.OK, item.getStatusCode());
    }

    @Test
    void testGetAllUserItems() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);

        ResponseEntity<Object> items = itemClient.getAllUserItems(1L);
        Assertions.assertEquals(HttpStatus.OK, items.getStatusCode());
    }

    @Test
    void testSearchItem() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                anyMap()
        )).thenReturn(mockResponse);

        ResponseEntity<Object> items = itemClient.searchItem("testSearch");
        Assertions.assertEquals(HttpStatus.OK, items.getStatusCode());
    }

    @Test
    void testCreateComment() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(mockResponse);
        TextCommentDto textCommentDto = new TextCommentDto("test comment");
        ResponseEntity<Object> comment = itemClient.createComment(1L, 1L, textCommentDto);
        Assertions.assertEquals(HttpStatus.OK, comment.getStatusCode());
    }
}
