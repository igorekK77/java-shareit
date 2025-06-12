package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapperTest {
    @Test
    public void testCreateComment() {
        CommentMapper mapper = new CommentMapper();
        User user = new User(1L, "testUser", "test@mail.ru");
        Item item = new Item(1L, "test1",
                "testDescription1", true, new User(3L, "test3", "test@mail.ru"),
                null);
        Comment comment = new Comment(null, "testComment", new Item(1L, "test1",
                "testDescription1", true, new User(3L, "test3", "test@mail.ru"),
                null), new User(1L, "testUser", "test@mail.ru"), LocalDateTime.now());
        Comment resultComment = mapper.createComment("testComment", user, item);
        Assertions.assertEquals(comment.getText(), resultComment.getText());
        Assertions.assertEquals(comment.getUser(), resultComment.getUser());
        Assertions.assertEquals(comment.getItem(), resultComment.getItem());
    }
}
