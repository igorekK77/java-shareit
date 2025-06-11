package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemRequestStorageTest {
    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;

    @Test
    void testCreateRequest() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequestWithOutId = new ItemRequest(null, "test description", user, dateTime);
        ItemRequest checkItemRequest = itemRequestStorage.save(itemRequestWithOutId);
        ItemRequest itemRequest = new ItemRequest(checkItemRequest.getId(), "test description", user,
                dateTime);

        Assertions.assertEquals(itemRequest, checkItemRequest);
    }

    @Test
    void testGetRequestById() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequestWithOutId = new ItemRequest(null, "test description", user, dateTime);
        ItemRequest itemRequest = itemRequestStorage.save(itemRequestWithOutId);
        Assertions.assertEquals(Optional.of(itemRequest), itemRequestStorage.findById(itemRequest.getId()));
    }

    @Test
    void testFindAllByUserId() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequestWithOutId1 = new ItemRequest(null, "test description", user, dateTime);
        ItemRequest itemRequestWithOutId2 = new ItemRequest(null, "test description2", user, dateTime);
        ItemRequest itemRequestWithOutId3 = new ItemRequest(null, "test description3", user, dateTime);
        ItemRequest itemRequest1 = itemRequestStorage.save(itemRequestWithOutId1);
        ItemRequest itemRequest2 = itemRequestStorage.save(itemRequestWithOutId2);
        ItemRequest itemRequest3 = itemRequestStorage.save(itemRequestWithOutId3);

        Assertions.assertEquals(List.of(itemRequest1, itemRequest2, itemRequest3),
                itemRequestStorage.findAllByRequesterId(user.getId()));
    }

    @Test
    void testFindAllByUserIdIdNot() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        User user2 = userStorage.save(new User(null, "testUser2", "test2@mail.ru"));
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequestWithOutId1 = new ItemRequest(null, "test description", user, dateTime);
        ItemRequest itemRequestWithOutId2 = new ItemRequest(null, "test description2", user2, dateTime);
        ItemRequest itemRequestWithOutId3 = new ItemRequest(null, "test description3", user2, dateTime);
        itemRequestStorage.save(itemRequestWithOutId1);
        ItemRequest itemRequest2 = itemRequestStorage.save(itemRequestWithOutId2);
        ItemRequest itemRequest3 = itemRequestStorage.save(itemRequestWithOutId3);

        Assertions.assertEquals(List.of(itemRequest2, itemRequest3),
                itemRequestStorage.findAllByRequester_IdNot(user.getId()));
    }
}
