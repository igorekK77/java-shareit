package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */

@Data
@NoArgsConstructor
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;
}
