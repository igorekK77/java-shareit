package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;

public class CommentDtoMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getItem(), comment.getUser().getName(),
                comment.getCreated());
    }
}
