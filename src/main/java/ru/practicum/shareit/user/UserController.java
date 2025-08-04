package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.error.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserDtoMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId){
        return userRepository.getUserById(userId);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody User user){
        return userDtoMapper.toDto(userRepository.addUser(user));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(NotUniqueEmailException e) {
        return new ErrorResponse("Ошибка параметра email", e.getMessage());
    }
}
