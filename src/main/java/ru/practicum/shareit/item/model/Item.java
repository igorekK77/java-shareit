package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */

@Data
@NoArgsConstructor
public class Item {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest itemRequest;

}
