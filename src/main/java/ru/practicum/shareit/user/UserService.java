package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public UserDto addUser(UserCreateDto userCreateDto) {
        return userRepository.addUser(userCreateDto);
    }

    public UserDto updateUser(Integer userId, UserUpdateDto userUpdateDto) {
        isUserExists(userId);
        return userRepository.updateUser(userId, userUpdateDto);

    }

    public UserDto getUserById(int userId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userDtoMapper.toDto(user);
    }

    public void deleteUser(Integer userId) {
        isUserExists(userId);
        userRepository.deleteUser(userId);
    }

    public void isUserExists(Integer userId) {
        getUserById(userId);
    }
}
