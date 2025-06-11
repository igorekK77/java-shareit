package ru.practicum.shareit.user;

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
    private UserService userService;

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
        UserDto user1 = new UserDto(1L, "Test1", "test1@mail.ru");
        UserDto user2 = new UserDto(2L, "Test2", "test2@mail.ru");
        when(userService.allUsers()).thenReturn(List.of(user1, user2));
        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test1"))
                .andExpect(jsonPath("$[0].email").value("test1@mail.ru"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Test2"))
                .andExpect(jsonPath("$[1].email").value("test2@mail.ru"));
        verify(userService, times(1)).allUsers();
    }

    @Test
    void testGetUserById() throws Exception {
        UserDto user1 = new UserDto(1L, "Test1", "test1@mail.ru");
        when(userService.getUserById(1L)).thenReturn(user1);
        mockMvc.perform(get("/users/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test1"))
                .andExpect(jsonPath("$.email").value("test1@mail.ru"));
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testCreateUser() throws Exception {
        CreateUserDto userDto = new CreateUserDto("TestNew1", "testNew1@mail.ru");
        String jsonCreateUserDto = objectMapper.writeValueAsString(userDto);
        when(userService.createUser(userDto)).thenReturn(new UserDto(1L, userDto.getName(), userDto.getEmail()));
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreateUserDto)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestNew1"))
                .andExpect(jsonPath("$.email").value("testNew1@mail.ru"));
        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto newUserDto = new UserDto(1L, "UpdateUser1", "updateUser1@mail.ru");
        String jsonUpdateUserDto = objectMapper.writeValueAsString(newUserDto);
        when(userService.updateUser(1L, newUserDto)).thenReturn(newUserDto);
        mockMvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdateUserDto)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("UpdateUser1"))
                .andExpect(jsonPath("$.email").value("updateUser1@mail.ru"));
        verify(userService, times(1)).updateUser(1L, newUserDto);
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteAllUsers() throws Exception {
        mockMvc.perform(delete("/users")).andExpect(status().isOk());
        verify(userService, times(1)).deleteAllUsers();
    }
}
