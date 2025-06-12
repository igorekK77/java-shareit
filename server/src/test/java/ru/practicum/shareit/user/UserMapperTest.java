package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

public class UserMapperTest {
    @Test
    void testToUserDto() {
        UserMapper userMapper = new UserMapper();
        User user1 = new User(1L, "Test1", "test1@mail.ru");
        UserDto userDto1 = new UserDto(1L, "Test1", "test1@mail.ru");

        Assertions.assertEquals(userDto1, userMapper.toUserDto(user1));
    }

    @Test
    void testToUser() {
        User user1 = new User(1L, "Test1", "test1@mail.ru");
        UserDto userDto1 = new UserDto(1L, "Test1", "test1@mail.ru");

        Assertions.assertEquals(user1, UserMapper.toUser(userDto1));
    }
}
