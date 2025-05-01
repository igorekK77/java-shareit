package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<UserDto> allUsers() {
        return userStorage.allUsers().stream().map(UserDtoMapper::toUserDto).toList();
    }

    public UserDto getUserById(Long userId) {
        return UserDtoMapper.toUserDto(userStorage.getUserById(userId));
    }

    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("Email должен быть указан!");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("Email указан в неправильном формате!");
        }

        return userStorage.createUser(UserDtoMapper.toUser(userDto));
    }

    public UserDto updateUser(Long userId, UserDto newUserDto) {
        return userStorage.updateUser(userId, UserDtoMapper.toUser(newUserDto));
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }
}
