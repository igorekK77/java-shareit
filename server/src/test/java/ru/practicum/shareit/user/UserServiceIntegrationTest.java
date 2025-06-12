package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final UserService userService;

    @Test
    void testCreateUser() {
        CreateUserDto userCreateDto = new CreateUserDto("Test1", "test1@mail.ru");
        UserDto totalUser = userService.createUser(userCreateDto);
        UserDto checkUser = new UserDto(totalUser.getId(), "Test1", "test1@mail.ru");
        Assertions.assertEquals(checkUser, totalUser);
    }

    @Test
    void testGetUserById() {
        CreateUserDto userCreateDto = new CreateUserDto("Test1", "test1@mail.ru");
        UserDto testUser = userService.createUser(userCreateDto);

        UserDto checkUser = new UserDto(testUser.getId(), "Test1", "test1@mail.ru");
        Assertions.assertEquals(checkUser, userService.getUserById(testUser.getId()));
    }
}
