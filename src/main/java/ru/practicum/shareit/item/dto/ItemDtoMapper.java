package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwnerId(item.getOwner().getId());

        return itemDto;
    }
}
