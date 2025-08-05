package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserRepository {

    UserDto addUser(UserCreateDto userCreateDto);

    UserDto getUserById(Integer id);

    boolean isEmailExists(String email);

    boolean isUserExists(Integer id);

    UserDto updateUser(Integer userId, UserUpdateDto userUpdateDto);

    void deleteUser(Integer userId);
}
