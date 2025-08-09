package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;


public interface UserRepository {

    User addUser(UserCreateDto userCreateDto);

    User getUserById(Integer id);

    boolean isEmailExists(String email);

    User updateUser(Integer userId, UserUpdateDto userUpdateDto);

    void deleteUser(Integer userId);

    boolean isUserExists(Integer userId);
}
