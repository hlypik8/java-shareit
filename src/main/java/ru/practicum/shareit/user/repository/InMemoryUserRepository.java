package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final UserDtoMapper userDtoMapper;

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 0;

    @Override
    public User addUser(UserCreateDto userCreateDto) {
        User user = userDtoMapper.toUser(userCreateDto);
        int id = getNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(Integer userId, UserUpdateDto userUpdateDto) {
        User user = users.get(userId);
        if (userUpdateDto.getName() != null) {
            user.setName(userUpdateDto.getName());
        }
        if (userUpdateDto.getEmail() != null) {
            user.setEmail(userUpdateDto.getEmail());
        }
        return user;
    }

    @Override
    public User getUserById(Integer userId) {
        return users.get(userId);
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

    public boolean isUserExists(Integer userId) {
        return users.get(userId) != null;
    }

    private Integer getNextId() {
        return nextId++;
    }
}
