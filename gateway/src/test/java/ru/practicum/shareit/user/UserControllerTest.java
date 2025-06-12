package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserDto userDto1 = new UserDto(1L, "Test1", "test1@mail.ru");
        UserDto userDto2 = new UserDto(2L, "Test2", "test2@mail.ru");
        String usersJson = objectMapper.writeValueAsString(List.of(userDto1, userDto2));
        ResponseEntity<Object> users = new ResponseEntity<>(usersJson, HttpStatus.OK);
        when(userClient.getUsers()).thenReturn(users);
        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test1"))
                .andExpect(jsonPath("$[0].email").value("test1@mail.ru"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Test2"))
                .andExpect(jsonPath("$[1].email").value("test2@mail.ru"));
        verify(userClient, times(1)).getUsers();
    }

    @Test
    void testGetUser() throws Exception {
        UserDto userDto1 = new UserDto(1L, "Test1", "test1@mail.ru");
        String userJson = objectMapper.writeValueAsString(userDto1);
        ResponseEntity<Object> user = new ResponseEntity<>(userJson, HttpStatus.OK);
        when(userClient.getUser(1L)).thenReturn(user);
        mockMvc.perform(get("/users/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test1"))
                .andExpect(jsonPath("$.email").value("test1@mail.ru"));
        verify(userClient, times(1)).getUser(1L);
    }

    @Test
    void testCreateUser() throws Exception {
        UserDto userDto1 = new UserDto(1L, "Test1", "test1@mail.ru");
        String userDtoJson = objectMapper.writeValueAsString(userDto1);
        CreateUserDto createUserDto = new CreateUserDto("Test1", "test1@mail.ru");
        String userJson = objectMapper.writeValueAsString(createUserDto);
        ResponseEntity<Object> user = new ResponseEntity<>(userDtoJson, HttpStatus.OK);
        when(userClient.createUser(createUserDto)).thenReturn(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test1"))
                .andExpect(jsonPath("$.email").value("test1@mail.ru"));
        verify(userClient, times(1)).createUser(createUserDto);
    }

    @Test
    void testCreateUserWithNotEmail() {
        CreateUserDto createUserDto = new CreateUserDto("Test1", null);
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(createUserDto));
    }

    @Test
    void testCreateUserWithEmailIncorrectFormat() {
        CreateUserDto createUserDto = new CreateUserDto("Test1", "test1");
        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(createUserDto));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto userDto1 = new UserDto(1L, "Test1", "test1@mail.ru");
        String userDtoJson = objectMapper.writeValueAsString(userDto1);
        ResponseEntity<Object> user = new ResponseEntity<>(userDtoJson, HttpStatus.OK);
        when(userClient.updateUser(1L, userDto1)).thenReturn(user);
        mockMvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoJson)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test1"))
                .andExpect(jsonPath("$.email").value("test1@mail.ru"));
        verify(userClient, times(1)).updateUser(1L, userDto1);
    }

    @Test
    void testDeleteUser() throws Exception {
        ResponseEntity<Object> user = new ResponseEntity<>(HttpStatus.OK);
        when(userClient.deleteUser(1L)).thenReturn(user);
        mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
    }

    @Test
    void testDeleteAllUsers() throws Exception {
        ResponseEntity<Object> user = new ResponseEntity<>(HttpStatus.OK);
        when(userClient.deleteAllUsers()).thenReturn(user);
        mockMvc.perform(delete("/users")).andExpect(status().isOk());
    }

}
