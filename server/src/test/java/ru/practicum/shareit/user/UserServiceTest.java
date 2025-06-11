package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;

    @Test
    void testAllUsers() {
        User user1 = new User(1L, "Test1", "test1@mail.ru");
        User user2 = new User(2L, "Test2", "test2@mail.ru");

        UserDto userDto1 = new UserDto(1L, "Test1", "test1@mail.ru");
        UserDto userDto2 = new UserDto(2L, "Test2", "test2@mail.ru");
        when(userStorage.findAll()).thenReturn(List.of(user1, user2));

        Assertions.assertEquals(List.of(userDto1, userDto2), userService.allUsers());
        verify(userStorage, times(1)).findAll();
    }

    @Test
    void testFindUserById() {
        User user1 = new User(1L, "Test1", "test1@mail.ru");
        UserDto userDto1 = new UserDto(1L, "Test1", "test1@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(user1));

        Assertions.assertEquals(userDto1, userService.getUserById(1L));
        verify(userStorage, times(1)).findById(1L);
    }

    @Test
    void testFindUserByIdWithNotFoundUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testCreateUser() {
        CreateUserDto userCreateDto = new CreateUserDto("Test1", "test1@mail.ru");
        User userWithOutId = new User(null, "Test1", "test1@mail.ru");
        User userWithId = new User(1L, "Test1", "test1@mail.ru");
        UserDto userDto = new UserDto(1L, "Test1", "test1@mail.ru");

        when(userStorage.save(userWithOutId)).thenReturn(userWithId);
        Assertions.assertEquals(userDto, userService.createUser(userCreateDto));
        verify(userStorage, times(1)).save(userWithOutId);
    }

    @Test
    void testCreateUserWithUsingEmail() {
        when(userStorage.findByEmail("test@mail.ru")).thenReturn(new User(12L, "test", "test@mail.ru"));
        Assertions.assertThrows(ConflictException.class, () -> userService.createUser(new CreateUserDto("test2",
                "test@mail.ru")));
    }

    @Test
    void testUpdateUser() {
        UserDto newUserDto = new UserDto(1L, "Test1", "test1@mail.ru");
        User userWithId = new User(1L, "Test1", "test1@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(userWithId));
        when(userStorage.save(userWithId)).thenReturn(userWithId);

        Assertions.assertEquals(newUserDto, userService.updateUser(1L, newUserDto));
        verify(userStorage, times(1)).save(userWithId);
    }

    @Test
    void testUpdateUserWithNotFoundUser() {
        UserDto newUserDto = new UserDto(1L, "Test1", "test1@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUser(1L,
                newUserDto));
        verify(userStorage, times(1)).findById(1L);
    }

    @Test
    void testUpdateUserWithUsingEmail() {
        User userWithId = new User(1L, "Test1", "test1@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(userWithId));
        when(userStorage.findByEmail("test@mail.ru")).thenReturn(new User(12L, "test", "test@mail.ru"));
        Assertions.assertThrows(ConflictException.class, () -> userService.updateUser(1L, new UserDto(1L,
                "test","test@mail.ru")));
    }

    @Test
    void testUpdateUserWithArgsNull() {
        UserDto newUserDto = new UserDto(1L, null, null);
        User userWithId = new User(1L, "Test1", "test1@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(userWithId));
        when(userStorage.save(userWithId)).thenReturn(userWithId);

        Assertions.assertEquals(newUserDto, userService.updateUser(1L, newUserDto));
        verify(userStorage, times(1)).save(userWithId);
    }

    @Test
    void testDeleteUser() {
        User userWithId = new User(1L, "Test1", "test1@mail.ru");
        when(userStorage.findById(1L)).thenReturn(Optional.of(userWithId));

        Assertions.assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userStorage, times(1)).findById(1L);
        verify(userStorage, times(1)).delete(userWithId);
    }

    @Test
    void testDeleteUserWithNotFoundUser() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void testDeleteAllUsers() {
        userService.deleteAllUsers();
        verify(userStorage, times(1)).deleteAll();
    }
}
