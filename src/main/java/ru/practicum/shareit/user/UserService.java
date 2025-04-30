package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<UserDto> allUsers() {
        return userStorage.allUsers();
    }

    public UserDto getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public UserDto createUser(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Email должен быть указан!");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email указан в неправильном формате!");
        }

        return userStorage.createUser(user);
    }

    public UserDto updateUser(Long userId, User newUser) {
        return userStorage.updateUser(userId, newUser);
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }
}
