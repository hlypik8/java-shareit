package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto addUser(UserCreateDto userCreateDto) {
        return userRepository.addUser(userCreateDto);
    }

    public UserDto updateUser(Integer userId, UserUpdateDto userUpdateDto) {
        userRepository.isUserExists(userId);
        return userRepository.updateUser(userId, userUpdateDto);
    }

    public UserDto getUserById(Integer userId) {
        userRepository.isUserExists(userId);
        return userRepository.getUserById(userId);
    }

    public void deleteUser(Integer userId){
        userRepository.isUserExists(userId);
        userRepository.deleteUser(userId);
    }
}
