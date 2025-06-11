package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private RequestClient requestClient;

    @InjectMocks
    private RequestController requestController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateRequest() throws Exception {
        ItemRequestDescriptionDto itemRequestDescriptionDto = new ItemRequestDescriptionDto("test description");
        String requestJson = objectMapper.writeValueAsString(itemRequestDescriptionDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "test description", 1L,
                LocalDateTime.now());
        String itemRequestDtoJson = objectMapper.writeValueAsString(itemRequestDto);
        ResponseEntity<Object> request = new ResponseEntity<>(itemRequestDtoJson, HttpStatus.OK);
        when(requestClient.createRequest(1L, itemRequestDescriptionDto)).thenReturn(request);

        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("test description"))
                .andExpect(jsonPath("$.userId").value(1));
        verify(requestClient).createRequest(1L, itemRequestDescriptionDto);
    }

    @Test
    void testGetRequestUser() throws Exception {
        ItemRequestWithAnswerDto itemRequestWithAnswerDto = new ItemRequestWithAnswerDto(1L,
                "test description", 1L, LocalDateTime.now(), null);
        String requestJson = objectMapper.writeValueAsString(List.of(itemRequestWithAnswerDto));
        ResponseEntity<Object> request = new ResponseEntity<>(requestJson, HttpStatus.OK);
        when(requestClient.getRequestUser(1L)).thenReturn(request);

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("test description"))
                .andExpect(jsonPath("$[0].userId").value(1));
        verify(requestClient).getRequestUser(1L);
    }

    @Test
    void testGetAllRequest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "test description", 1L,
                LocalDateTime.now());
        String requestJson = objectMapper.writeValueAsString(List.of(itemRequestDto));
        ResponseEntity<Object> request = new ResponseEntity<>(requestJson, HttpStatus.OK);
        when(requestClient.getAllRequests(1L)).thenReturn(request);

        mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("test description"))
                .andExpect(jsonPath("$[0].userId").value(1));
        verify(requestClient).getAllRequests(1L);
    }

    @Test
    void testGetRequest() throws Exception {
        ItemRequestWithAnswerDto itemRequestWithAnswerDto = new ItemRequestWithAnswerDto(1L,
                "test description", 1L, LocalDateTime.now(), null);
        String requestJson = objectMapper.writeValueAsString(itemRequestWithAnswerDto);
        ResponseEntity<Object> request = new ResponseEntity<>(requestJson, HttpStatus.OK);
        when(requestClient.getRequest(1L, 1L)).thenReturn(request);

        mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("test description"))
                .andExpect(jsonPath("$.userId").value(1));
        verify(requestClient).getRequest(1L, 1L);
    }

}
