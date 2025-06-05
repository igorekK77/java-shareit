package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoWithDates {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private LocalDateTime lastBooking;

    private LocalDateTime endLastBooking;

    private LocalDateTime nextBooking;

    private LocalDateTime endNextBooking;

    private List<Comment> comments;
}
