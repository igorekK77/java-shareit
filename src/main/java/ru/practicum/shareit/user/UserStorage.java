package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    List<User> allUsers();

    User getUserById(Long userId);

    User createUser(User user);

    User updateUser(Long userId, User newUser);

    void deleteUser(Long userId);

    void deleteAllUsers();
}
