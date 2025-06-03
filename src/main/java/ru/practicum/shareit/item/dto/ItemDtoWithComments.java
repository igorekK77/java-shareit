package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoWithComments {
    private Long id;

    private String name;

    private String description;

    private Boolean available;


    private List<Comment> comments;

    public static ItemDtoWithComments toItemDtoWithComments(Item item) {
        ItemDtoWithComments itemDtoWithComments = new ItemDtoWithComments();
        itemDtoWithComments.setId(item.getId());
        itemDtoWithComments.setName(item.getName());
        itemDtoWithComments.setDescription(item.getDescription());
        itemDtoWithComments.setAvailable(item.getAvailable());
        return itemDtoWithComments;
    }
}
