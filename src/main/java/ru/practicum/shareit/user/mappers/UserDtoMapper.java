package ru.practicum.shareit.user.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserDtoMapper {

    public UserDto toDto(User user){
        return new UserDto(user.getName(), user.getEmail());
    }
}
