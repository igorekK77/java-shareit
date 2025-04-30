package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserDtoMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }
}
