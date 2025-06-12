package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.TextCommentDto;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateItem() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true, 1L);
        String itemDtoJson = objectMapper.writeValueAsString(itemDto);
        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", true,
                null);
        String itemJson = objectMapper.writeValueAsString(itemCreateDto);
        ResponseEntity<Object> item = new ResponseEntity<>(itemDtoJson, HttpStatus.OK);
        when(itemClient.createItem(1L, itemCreateDto)).thenReturn(item);
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(itemJson)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test1"))
                .andExpect(jsonPath("$.description").value("testDescription1"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.userId").value(1L));
        verify(itemClient, times(1)).createItem(1L, itemCreateDto);
    }

    @Test
    void testCreateItemWithNotName() {
        ItemCreateDto itemCreateDto = new ItemCreateDto(null, "testDescription1", true,
                null);
        Assertions.assertThrows(ValidationException.class, () -> itemController.createItem(1L, itemCreateDto));
    }

    @Test
    void testCreateItemWithNotDescription() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", null, true,
                null);
        Assertions.assertThrows(ValidationException.class, () -> itemController.createItem(1L, itemCreateDto));
    }

    @Test
    void testCreateItemWithNotAvailable() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", null,
                null);
        Assertions.assertThrows(ValidationException.class, () -> itemController.createItem(1L, itemCreateDto));
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true, 1L);
        String itemDtoJson = objectMapper.writeValueAsString(itemDto);
        ResponseEntity<Object> item = new ResponseEntity<>(itemDtoJson, HttpStatus.OK);
        when(itemClient.updateItem(1L, 1L, itemDto)).thenReturn(item);
        mockMvc.perform(patch("/items/1").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content(itemDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test1"))
                .andExpect(jsonPath("$.description").value("testDescription1"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.userId").value(1L));
        verify(itemClient, times(1)).updateItem(1L, 1L, itemDto);
    }

    @Test
    void testGetItemById() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true, 1L);
        String itemDtoJson = objectMapper.writeValueAsString(itemDto);
        ResponseEntity<Object> item = new ResponseEntity<>(itemDtoJson, HttpStatus.OK);
        when(itemClient.getItemById(1L, 1L)).thenReturn(item);

        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test1"))
                .andExpect(jsonPath("$.description").value("testDescription1"))
                .andExpect(jsonPath("$.available").value(true));
        verify(itemClient, times(1)).getItemById(1L, 1L);
    }

    @Test
    void testGetAllUserItems() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "test1", "testDescription1", true, 1L);
        String itemDtoJson = objectMapper.writeValueAsString(List.of(itemDto));
        ResponseEntity<Object> item = new ResponseEntity<>(itemDtoJson, HttpStatus.OK);
        when(itemClient.getAllUserItems(1L)).thenReturn(item);

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("test1"))
                .andExpect(jsonPath("$[0].description").value("testDescription1"))
                .andExpect(jsonPath("$[0].available").value(true));
        verify(itemClient, times(1)).getAllUserItems(1L);
    }

    @Test
    void testSearchItem() throws Exception {
        ItemDto itemDto1 = new ItemDto(1L, "testSearch1", "Description1",
                true, 1L);
        ItemDto itemDto2 = new ItemDto(11L, "test", "testSearch1Description", true,
                2L);
        String itemDtoJson = objectMapper.writeValueAsString(List.of(itemDto1, itemDto2));
        ResponseEntity<Object> item = new ResponseEntity<>(itemDtoJson, HttpStatus.OK);
        when(itemClient.searchItem("testSearch")).thenReturn(item);

        mockMvc.perform(get("/items/search?text=testSearch")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("testSearch1"))
                .andExpect(jsonPath("$[0].description").value("Description1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].name").value("test"))
                .andExpect(jsonPath("$[1].description").value("testSearch1Description"))
                .andExpect(jsonPath("$[1].available").value(true))
                .andExpect(jsonPath("$[1].userId").value(2));
        verify(itemClient, times(1)).searchItem("testSearch");
    }

    @Test
    void testCreateComment() throws Exception {
        TextCommentDto textCommentDto = new TextCommentDto("test comment");
        String jsonCommentDto = objectMapper.writeValueAsString(textCommentDto);
        ResponseEntity<Object> item = new ResponseEntity<>(jsonCommentDto, HttpStatus.OK);
        when(itemClient.createComment(1L, 2L, textCommentDto)).thenReturn(item);
        mockMvc.perform(post("/items/2/comment").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1).content(jsonCommentDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("test comment"));
        verify(itemClient, times(1)).createComment(1L, 2L, textCommentDto);
    }

    @Test
    void testCreateCommentWithEmptyText() throws Exception {
        TextCommentDto textCommentDto = new TextCommentDto("");
        Assertions.assertThrows(ValidationException.class, () -> itemController.createComment(1L, 2L,
                textCommentDto));
    }
}
