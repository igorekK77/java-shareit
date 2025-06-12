package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

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
    void testCreate() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto("test1", "testDescription1", true,
                null);
        when(itemService.createItem(1L, itemCreateDto)).thenReturn(new ItemDto(1L, "test1",
                "testDescription1", true, 1L, null));
        String jsonItemCreateDto = objectMapper.writeValueAsString(itemCreateDto);
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                .content(jsonItemCreateDto)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test1"))
                .andExpect(jsonPath("$.description").value("testDescription1"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.userId").value(1L));
        verify(itemService, times(1)).createItem(1L, itemCreateDto);
    }

    @Test
    void testUpdate() throws Exception {
        ItemDto newItemDto = new ItemDto(1L, "newTest1", "testDescription1New", true,
                1L, 13L);
        String jsonItemDto = objectMapper.writeValueAsString(newItemDto);
        when(itemService.updateItem(1L, 1L, newItemDto)).thenReturn(new ItemDto(1L, "newTest1",
                "testDescription1New", true, 1L, 13L));
        mockMvc.perform(patch("/items/1").header("X-Sharer-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON).content(jsonItemDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("newTest1"))
                .andExpect(jsonPath("$.description").value("testDescription1New"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.requestId").value(13L));
        verify(itemService, times(1)).updateItem(1L, 1L, newItemDto);
    }

    @Test
    void testGetItemById() throws Exception {
        when(itemService.getItemById(1L, 1L)).thenReturn(new ItemDtoWithBookings(1L, "test1",
                "testDescription1", true, new User(1L, "test1", "test1@mail.ru"),
                null, null, null));
        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test1"))
                .andExpect(jsonPath("$.description").value("testDescription1"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.owner.id").value(1));
        verify(itemService, times(1)).getItemById(1L, 1L);
    }

    @Test
    void testGetAllUserItems() throws Exception {
        ItemDtoWithBookings itemDtoWithBookings1 = new ItemDtoWithBookings(1L, "test1",
                "testDescription1", true, new User(1L, "test1", "test1@mail.ru"),
                null, null, null);

        ItemDtoWithBookings itemDtoWithBookings2 = new ItemDtoWithBookings(2L, "test2",
                "testDescription2", true, new User(1L, "test1", "test1@mail.ru"),
                null, null, null);

        when(itemService.getAllUserItems(1L)).thenReturn(List.of(itemDtoWithBookings1, itemDtoWithBookings2));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("test1"))
                .andExpect(jsonPath("$[0].description").value("testDescription1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].owner.id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("test2"))
                .andExpect(jsonPath("$[1].description").value("testDescription2"))
                .andExpect(jsonPath("$[1].available").value(true))
                .andExpect(jsonPath("$[1].owner.id").value(1));
        verify(itemService, times(1)).getAllUserItems(1L);
    }

    @Test
    void testSearchItems() throws Exception {
        ItemDto itemDto1 = new ItemDto(1L, "testSearch1", "testDescription1", true,
                1L, 11L);
        ItemDto itemDto2 = new ItemDto(11L, "test", "testSearch1Description", true,
                2L, null);
        when(itemService.searchItem("testSearch")).thenReturn(List.of(itemDto1, itemDto2));

        mockMvc.perform(get("/items/search?text=testSearch")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("testSearch1"))
                .andExpect(jsonPath("$[0].description").value("testDescription1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].name").value("test"))
                .andExpect(jsonPath("$[1].description").value("testSearch1Description"))
                .andExpect(jsonPath("$[1].available").value(true))
                .andExpect(jsonPath("$[1].userId").value(2));
        verify(itemService, times(1)).searchItem("testSearch");
    }

    @Test
    void testSearchItemsWithEmptyText() throws Exception {
        mockMvc.perform(get("/items/search?text=")).andExpect(status().isOk());
    }

    @Test
    void testCreateComment() throws Exception {
        TextCommentDto textCommentDto = new TextCommentDto("test comment");
        String jsonCommentDto = objectMapper.writeValueAsString(textCommentDto);

        when(itemService.createComment(1L, 2L, "test comment")).thenReturn(new CommentDto(1L,
                "test comment", new Item(2L, "test", "testD", true, new User(11L,
                "testUser", "testUser@mail.ru"), null), "TestName",
                LocalDateTime.now()));

        mockMvc.perform(post("/items/2/comment").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1).content(jsonCommentDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("test comment"))
                .andExpect(jsonPath("$.item.id").value(2))
                .andExpect(jsonPath("$.item.name").value("test"))
                .andExpect(jsonPath("$.item.description").value("testD"))
                .andExpect(jsonPath("$.item.owner.id").value(11))
                .andExpect(jsonPath("$.authorName").value("TestName"));
        verify(itemService, times(1)).createComment(1L, 2L, "test comment");
    }
}
