package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createRequest() throws Exception {
        ItemRequestDescriptionDto itemRequestDescriptionDto = new ItemRequestDescriptionDto("test description");
        String requestJson = objectMapper.writeValueAsString(itemRequestDescriptionDto);
        when(itemRequestService.createItemRequest(1L, itemRequestDescriptionDto)).thenReturn(
                new ItemRequestDto(1L, "test description", 1L, LocalDateTime.now()));

        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("test description"))
                .andExpect(jsonPath("$.userId").value(1));
        verify(itemRequestService).createItemRequest(1L, itemRequestDescriptionDto);
    }

    @Test
    void getRequestUser() throws Exception {
        ItemRequestWithAnswerDto itemRequestWithAnswerDto1 = new ItemRequestWithAnswerDto(1L,
                "test description", 1L, LocalDateTime.now(), null);
        ItemRequestWithAnswerDto itemRequestWithAnswerDto2 = new ItemRequestWithAnswerDto(2L,
                "test description2", 1L, LocalDateTime.now(), null);
        when(itemRequestService.getRequestUser(1L)).thenReturn(List.of(itemRequestWithAnswerDto1,
                itemRequestWithAnswerDto2));
        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("test description"))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("test description2"))
                .andExpect(jsonPath("$[1].userId").value(1));
        verify(itemRequestService).getRequestUser(1L);
    }

    @Test
    void getAllRequests() throws Exception {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "test description", 2L,
                LocalDateTime.now());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "test description2", 3L,
                LocalDateTime.now());
        when(itemRequestService.getAllRequests(1L)).thenReturn(List.of(itemRequestDto1, itemRequestDto2));

        mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("test description"))
                .andExpect(jsonPath("$[0].userId").value(2))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("test description2"))
                .andExpect(jsonPath("$[1].userId").value(3));
        verify(itemRequestService).getAllRequests(1L);
    }

    @Test
    void getRequest() throws Exception {
        ItemRequestWithAnswerDto itemRequestWithAnswerDto = new ItemRequestWithAnswerDto(1L,
                "test description", 1L, LocalDateTime.now(), null);
        when(itemRequestService.getRequestById(1L, 1L)).thenReturn(itemRequestWithAnswerDto);

        mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("test description"))
                .andExpect(jsonPath("$.userId").value(1));
        verify(itemRequestService).getRequestById(1L, 1L);
    }
}
