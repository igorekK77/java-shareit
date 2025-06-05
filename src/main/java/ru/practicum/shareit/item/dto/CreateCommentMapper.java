package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CreateCommentMapper {
    public static Comment createComment(String text, User user, Item item) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());
        comment.setUser(user);
        comment.setItem(item);
        return comment;
    }
}
