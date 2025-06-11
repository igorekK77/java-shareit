package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemStorageTest {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Test
    void testFindById() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        Item itemWithOutId = new Item(null, "test1", "testDescription1", true, user,
                null);
        Item checkItem = itemStorage.save(itemWithOutId);
        Item item = new Item(checkItem.getId(), "test1", "testDescription1", true, user,
                null);

        Assertions.assertEquals(Optional.of(item), itemStorage.findById(checkItem.getId()));
    }

    @Test
    void testCreate() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        Item itemWithOutId = new Item(null, "test1", "testDescription1", true, user,
                null);
        Item checkItem = itemStorage.save(itemWithOutId);
        Item item = new Item(checkItem.getId(), "test1", "testDescription1", true,
                user, null);

        Assertions.assertEquals(item, checkItem);
    }

    @Test
    void testGetUserItems() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        Item item = itemStorage.save(new Item(null, "test1", "testDescription1", true,
                user, null));
        Item item2 = itemStorage.save(new Item(null, "test2", "testDescription2", true,
                user, null));
        Assertions.assertEquals(List.of(item, item2), itemStorage.findAllByOwnerId(user.getId()));
    }

    @Test
    void testSearch() {
        User user = userStorage.save(new User(null, "testUser", "test@mail.ru"));
        Item item = itemStorage.save(new Item(null, "testSearch1", "testDescription1", true,
                user, null));
        Item item2 = itemStorage.save(new Item(null, "test2", "testSearch2", true,
                user, null));

        Assertions.assertEquals(List.of(item, item2), itemStorage
                .findAllByAvailableAndNameContainingIgnoreCaseOrAvailableAndDescriptionContainingIgnoreCase(
                        true, "testSearch", true, "testSearch"));
    }
}
