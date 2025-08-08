package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.error.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Создание нового пользователя");
        UserDto createdUser = userService.addUser(userCreateDto);
        log.info("Пользователь создан: ID={}, Email={}", createdUser.getId(), createdUser.getEmail());
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Integer userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Обновление пользователя ID {}", userId);
        UserDto updatedUser = userService.updateUser(userId, userUpdateDto);
        log.debug("Пользователь обновлён: {}", updatedUser.toString());
        return updatedUser;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) {
        log.info("Запрос пользователя ID {}", userId);
        UserDto user = userService.getUserById(userId);
        log.debug("Получен пользователь: {}", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        log.info("Удаление пользователя ID {}", userId);
        userService.deleteUser(userId);
        log.debug("Пользователь ID {} удалён", userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(NotUniqueEmailException e) {
        log.error("Ошибка уникальности email: {}", e.getMessage());
        return new ErrorResponse("Ошибка параметра email", e.getMessage());
    }
}
