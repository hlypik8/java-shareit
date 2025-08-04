package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserRepository {

    User addUser(User user);

    User getUserById(Integer id);

    boolean isEmailExists(String email);
}
