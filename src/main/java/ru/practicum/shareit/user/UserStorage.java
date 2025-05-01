package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    List<User> allUsers();

    User getUserById(Long userId);

    UserDto createUser(User user);

    UserDto updateUser(Long userId, User newUser);

    void deleteUser(Long userId);

    void deleteAllUsers();
}
