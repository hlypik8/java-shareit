package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto addUser(UserCreateDto userCreateDto) {
        log.info("Создание нового пользователя");

        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new NotUniqueEmailException("Пользователь с таким email-адресом уже существует");
        }

        User createdUser = userRepository.save(UserDtoMapper.toUser(userCreateDto));

        log.info("Пользователь успешно создан: ID={}, Email={}", createdUser.getId(), createdUser.getEmail());

        return UserDtoMapper.toDto(createdUser);
    }

    public UserDto updateUser(Integer userId, UserUpdateDto userUpdateDto) throws NotFoundException {
        log.info("Обновление пользователя ID {}", userId);

        User user = getUserById(userId);

        if (userUpdateDto.hasEmail() && !userUpdateDto.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(userUpdateDto.getEmail())) {
                throw new NotUniqueEmailException("Пользователь с таким email-адресом уже существует");
            }
        }

        User updatedUser = UserDtoMapper.updateUserFields(user, userUpdateDto);
        updatedUser = userRepository.save(updatedUser);

        log.info("Пользователь обновлён: {}", updatedUser);
        return UserDtoMapper.toDto(updatedUser);

    }

    public UserDto getUserDtoById(int userId) throws NotFoundException {
        log.info("Запрос пользователя ID {}", userId);

        User user = getUserById(userId);

        log.info("Получен пользователь: {}", user.toString());

        return UserDtoMapper.toDto(user);
    }

    public void deleteUser(Integer userId) throws NotFoundException {
        log.info("Удаление пользователя ID {}", userId);

        if (!isUserExists(userId)) {
            log.warn("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        log.info("Пользователь ID {} успешно удалён", userId);

        userRepository.delete(userRepository.findById(userId).get());
    }

    public User getUserById(Integer userId) throws NotFoundException {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID: " + userId + " не найден"));
    }

    public boolean isUserExists(Integer userId) {
        return userRepository.existsById(userId);
    }
}
