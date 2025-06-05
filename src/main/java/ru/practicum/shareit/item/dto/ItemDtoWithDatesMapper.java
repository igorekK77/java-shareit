package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;

public class ItemDtoWithDatesMapper {
    public static ItemDtoWithDates toItemDtoWithDates(Item item) {
        ItemDtoWithDates itemDtoWithDates = new ItemDtoWithDates();
        itemDtoWithDates.setId(item.getId());
        itemDtoWithDates.setName(item.getName());
        itemDtoWithDates.setDescription(item.getDescription());
        itemDtoWithDates.setAvailable(item.getAvailable());
        itemDtoWithDates.setOwner(item.getOwner());

        return itemDtoWithDates;
    }
}
