package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<UserDto> allUsers() {
        return userStorage.findAll().stream().map(UserMapper::toUserDto).toList();
    }

    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!")));
    }

    public UserDto createUser(CreateUserDto userDto) {
        User user = userStorage.findByEmail(userDto.getEmail());
        if (user != null) {
            throw new ConflictException("Пользователь с таким Email уже существует!");
        }

        return UserMapper.toUserDto(userStorage.save(CreateUserDto.toUser(userDto)));
    }

    public UserDto updateUser(Long userId, UserDto newUserDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID = " +
                userId + " не найден!"));
        if (newUserDto.getEmail() != null) {
            User userCheckEmail = userStorage.findByEmail(newUserDto.getEmail());
            if (userCheckEmail != null) {
                throw new ConflictException("Пользователь с таким Email уже существует!");
            }
        }
        if (newUserDto.getEmail() == null) {
            newUserDto.setEmail(user.getEmail());
        }
        if (newUserDto.getName() == null) {
            newUserDto.setName(user.getName());
        }
        User updateUser = UserMapper.toUser(newUserDto);
        updateUser.setId(userId);
        return UserMapper.toUserDto(userStorage.save(updateUser));
    }

    public void deleteUser(Long userId) {
        userStorage.delete(userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID = " + userId + " не найден!")));
    }

    public void deleteAllUsers() {
        userStorage.deleteAll();
    }
}
