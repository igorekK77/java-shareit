package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    private final UserStorage userStorage;

    @Test
    void testGetAllUsers() {
        User userWithOutId1 = new User(null, "Test1", "test1@mail.ru");
        User testUser1 = userStorage.save(userWithOutId1);

        User userWithOutId2 = new User(null, "Test2", "test2@mail.ru");
        User testUser2 = userStorage.save(userWithOutId2);

        User user1 = new User(testUser1.getId(), "Test1", "test1@mail.ru");
        User user2 = new User(testUser2.getId(), "Test2", "test2@mail.ru");

        Assertions.assertEquals(List.of(user1, user2), userStorage.findAll());
    }

    @Test
    void testGetUserById() {
        User userWithOutId1 = new User(null, "Test1", "test1@mail.ru");
        User testUser1 = userStorage.save(userWithOutId1);

        User user1 = new User(testUser1.getId(), "Test1", "test1@mail.ru");
        Assertions.assertEquals(Optional.of(user1), userStorage.findById(user1.getId()));
    }

    @Test
    void testGetUserByEmail() {
        User userWithOutId1 = new User(null, "Test1", "test1@mail.ru");
        User testUser1 = userStorage.save(userWithOutId1);

        User user1 = new User(testUser1.getId(), "Test1", "test1@mail.ru");
        Assertions.assertEquals(user1, userStorage.findByEmail(userWithOutId1.getEmail()));
    }

    @Test
    void testCreateUser() {
        User userWithOutId = new User(null, "Test1", "test1@mail.ru");
        User testUser = userStorage.save(userWithOutId);

        User user = new User(testUser.getId(), "Test1", "test1@mail.ru");

        Assertions.assertEquals(user, testUser);
    }

    @Test
    void testUpdateUser() {
        User userWithOutId1 = new User(null, "Test1", "test1@mail.ru");
        User testUser1 = userStorage.save(userWithOutId1);

        User userUpdateWithOutId = new User(testUser1.getId(), "Test2", "test2@mail.ru");
        User updatedUser = userStorage.save(userUpdateWithOutId);

        Assertions.assertEquals(userUpdateWithOutId, updatedUser);
    }

    @Test
    void testDeleteUser() {
        User userWithOutId1 = new User(null, "Test1", "test1@mail.ru");
        User testUser1 = userStorage.save(userWithOutId1);

        User user1 = new User(testUser1.getId(), "Test1", "test1@mail.ru");
        userStorage.delete(user1);
        Assertions.assertEquals(0, userStorage.findAll().size());
    }

    @Test
    void testDeleteAllUsers() {
        User userWithOutId1 = new User(null, "Test1", "test1@mail.ru");
        User testUser1 = userStorage.save(userWithOutId1);

        User userWithOutId2 = new User(null, "Test2", "test2@mail.ru");
        User testUser2 = userStorage.save(userWithOutId2);

        User user1 = new User(testUser1.getId(), "Test1", "test1@mail.ru");
        User user2 = new User(testUser2.getId(), "Test2", "test2@mail.ru");

        userStorage.delete(user1);
        userStorage.delete(user2);
        Assertions.assertEquals(0, userStorage.findAll().size());
    }

}
