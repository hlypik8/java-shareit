package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final UserDtoMapper userDtoMapper;

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 0;

    @Override
    public UserDto addUser(UserCreateDto userCreateDto) {
        User user = userDtoMapper.toUser(userCreateDto);
        int id = getNextId();
        user.setId(id);
        users.put(id, user);
        return userDtoMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(Integer userId, UserUpdateDto userUpdateDto) {
        User user = users.get(userId);
        if (userUpdateDto.getName() != null) {
            user.setName(userUpdateDto.getName());
        }
        if (userUpdateDto.getEmail() != null) {
            user.setEmail(userUpdateDto.getEmail());
        }
        return userDtoMapper.toDto(user);
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void deleteUser(Integer userId) {
        users.remove(userId);
    }

    @Override
    public boolean isEmailExists(String email) {
        if (users.values().stream()
                .anyMatch(user -> email.equalsIgnoreCase(user.getEmail()))) {
            throw new NotUniqueEmailException("Email должен быть уникальным");
        } else {
            return false;
        }
    }

    private Integer getNextId() {
        return nextId++;
    }
}
