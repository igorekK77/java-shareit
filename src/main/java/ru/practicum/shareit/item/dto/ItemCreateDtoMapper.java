package ru.practicum.shareit.item.dto;
import ru.practicum.shareit.item.Item;

public class ItemCreateDtoMapper {
    public static Item toItem(ItemCreateDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
