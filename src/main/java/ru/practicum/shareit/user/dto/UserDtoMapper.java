package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

@Component
public class UserDtoMapper {

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
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
