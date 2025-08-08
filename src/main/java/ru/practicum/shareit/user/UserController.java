package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.error.exceptions.NotFoundException;
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
        return userService.addUser(userCreateDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Integer userId,
                              @Valid @RequestBody UserUpdateDto userUpdateDto) throws NotFoundException {
        return userService.updateUser(userId, userUpdateDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) throws NotFoundException {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) throws NotFoundException {
        userService.deleteUser(userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(NotUniqueEmailException e) {
        log.error("Ошибка уникальности email: {}", e.getMessage());
        return new ErrorResponse("Ошибка параметра email", e.getMessage());
    }
}
