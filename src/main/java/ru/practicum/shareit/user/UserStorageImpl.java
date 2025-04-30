package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> userRepository = new HashMap<>();
    private long userCounter = 0;

    @Override
    public List<UserDto> allUsers() {
        return new ArrayList<>(userRepository.values().stream().map(UserDtoMapper::toUserDto).toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (!userRepository.containsKey(userId)) {
            throw new NotFoundException("Пользователя с ID = " + userId + " не существует!");
        }
        return UserDtoMapper.toUserDto(userRepository.get(userId));
    }

    @Override
    public UserDto createUser(User user) {
        userRepository.values().forEach(user1 -> {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Email уже используется!");
            }
        });
        user.setId(userCounter);
        userRepository.put(userCounter, user);
        userCounter++;
        return UserDtoMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, User newUser) {
        if (!userRepository.containsKey(userId)) {
            throw new NotFoundException("Пользователя с ID = " + newUser.getId() + " не существует!");
        }

        User user = userRepository.get(userId);
        if (newUser.getName() != null && !newUser.getName().equals(user.getName())) {
            user.setName(newUser.getName());
        }

        if (newUser.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            userRepository.values().forEach(user1 -> {
                if (user1.getEmail().equals(newUser.getEmail())) {
                    throw new ValidationException("Email уже используется!");
                }
            });
            user.setEmail(newUser.getEmail());
        }

        return UserDtoMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.containsKey(userId)) {
            throw new NotFoundException("Пользователя с ID = " + userId + " не существует!");
        }

        userRepository.remove(userId);
    }

    @Override
    public void deleteAllUsers() {
        if (!userRepository.isEmpty()) {
            userRepository.clear();
            userCounter = 0;
        }
    }
}
