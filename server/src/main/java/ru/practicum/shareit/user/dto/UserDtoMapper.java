package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

@UtilityClass
public class UserDtoMapper {

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public User toUser(UserCreateDto userCreateDto) {
        return new User(null, userCreateDto.getName(), userCreateDto.getEmail());
    }

    public User updateUserFields(User user, UserUpdateDto userUpdateDto) {
        if (userUpdateDto.hasName()) {
            user.setName(userUpdateDto.getName());
        }
        if (userUpdateDto.hasEmail()) {
            user.setEmail(userUpdateDto.getEmail());
        }

        return user;
    }
}
