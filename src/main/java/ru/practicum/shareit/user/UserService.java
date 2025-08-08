package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public UserDto addUser(UserCreateDto userCreateDto) {
        log.info("Создание нового пользователя");
        UserDto createdUser = userDtoMapper.toDto(userRepository.addUser(userCreateDto));
        log.info("Пользователь успешно создан: ID={}, Email={}", createdUser.getId(), createdUser.getEmail());
        return createdUser;
    }

    public UserDto updateUser(Integer userId, UserUpdateDto userUpdateDto) throws NotFoundException {
        log.info("Обновление пользователя ID {}", userId);
        if (!isUserExists(userId)) {
            log.warn("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        UserDto updatedUser = userDtoMapper.toDto(userRepository.updateUser(userId, userUpdateDto));
        log.debug("Пользователь обновлён: {}", updatedUser.toString());
        return updatedUser;

    }

    public UserDto getUserById(int userId) throws NotFoundException {
        log.info("Запрос пользователя ID {}", userId);

        if (!isUserExists(userId)) {
            log.warn("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        User user = userRepository.getUserById(userId);
        log.debug("Получен пользователь: {}", user.toString());

        return userDtoMapper.toDto(user);
    }

    public void deleteUser(Integer userId) throws NotFoundException {
        log.info("Удаление пользователя ID {}", userId);

        if (!isUserExists(userId)) {
            log.warn("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        log.debug("Пользователь ID {} удалён", userId);

        userRepository.deleteUser(userId);
    }

    public boolean isUserExists(Integer userId) {
        return userRepository.isUserExists(userId);
    }
}
