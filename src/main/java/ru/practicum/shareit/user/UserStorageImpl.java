package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> userRepository = new HashMap<>();
    private final Set<String> usedEmail = new HashSet<>();
    private long userCounter = 0;

    @Override
    public List<User> allUsers() {
        return new ArrayList<>(userRepository.values());
    }

    @Override
    public User getUserById(Long userId) {
        if (!userRepository.containsKey(userId)) {
            throw new NotFoundException("Пользователя с ID = " + userId + " не существует!");
        }
        return userRepository.get(userId);
    }

    @Override
    public UserDto createUser(User user) {
        if (usedEmail.contains(user.getEmail())) {
            throw new ConflictException("Email уже используется!");
        }
        user.setId(userCounter);
        userRepository.put(userCounter, user);
        usedEmail.add(user.getEmail());
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
            if (usedEmail.contains(newUser.getEmail())) {
                throw new ConflictException("Email уже используется!");
            }
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
