package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.user.User;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoWithBookings {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<Comment> comments;
}
