package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private User sampleUserNoId() {
        User u = new User();
        u.setName("Иван");
        u.setEmail("ivan@example.com");
        return u;
    }


    private User sampleUserWithId(int id, String name, String email) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        return u;
    }

    @Test
    void addUser() {
        UserService service = new UserService(userRepository);

        UserCreateDto createDto = mock(UserCreateDto.class);
        when(createDto.getEmail()).thenReturn("new@example.com");

        User toSave = sampleUserNoId();
        toSave.setEmail("new@example.com");

        User saved = sampleUserWithId(1, "Иван", "new@example.com");

        UserDto expectedDto = mock(UserDto.class);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        try (MockedStatic<UserDtoMapper> mapper = Mockito.mockStatic(UserDtoMapper.class)) {
            mapper.when(() -> UserDtoMapper.toUser(createDto)).thenReturn(toSave);
            when(userRepository.save(toSave)).thenReturn(saved);
            mapper.when(() -> UserDtoMapper.toDto(saved)).thenReturn(expectedDto);

            UserDto result = service.addUser(createDto);

            assertSame(expectedDto, result);
            verify(userRepository).existsByEmail("new@example.com");
            verify(userRepository).save(toSave);
        }
    }

    @Test
    void addUser_emailAlreadyExists() {
        UserService service = new UserService(userRepository);

        UserCreateDto createDto = mock(UserCreateDto.class);
        when(createDto.getEmail()).thenReturn("exists@example.com");

        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        assertThrows(NotUniqueEmailException.class, () -> service.addUser(createDto));

        verify(userRepository).existsByEmail("exists@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_userNotFound() {
        UserService service = new UserService(userRepository);

        int userId = 99;
        UserUpdateDto updateDto = mock(UserUpdateDto.class);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateUser(userId, updateDto));

        verify(userRepository).findById(userId);
    }

    @Test
    void updateUser_emailChangedButAlreadyExists() {
        UserService service = new UserService(userRepository);

        int userId = 2;
        User existing = sampleUserWithId(userId, "Пётр", "old@example.com");

        UserUpdateDto updateDto = mock(UserUpdateDto.class);
        when(updateDto.hasEmail()).thenReturn(true);
        when(updateDto.getEmail()).thenReturn("conflict@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("conflict@example.com")).thenReturn(true);

        assertThrows(NotUniqueEmailException.class, () -> service.updateUser(userId, updateDto));

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("conflict@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_success() throws NotFoundException {
        UserService service = new UserService(userRepository);

        int userId = 3;
        User existing = sampleUserWithId(userId, "Ольга", "olga@old.com");

        UserUpdateDto updateDto = mock(UserUpdateDto.class);
        when(updateDto.hasEmail()).thenReturn(true);
        when(updateDto.getEmail()).thenReturn("olga@new.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("olga@new.com")).thenReturn(false);

        User updated = sampleUserWithId(userId, "Ольга", "olga@new.com");
        User saved = sampleUserWithId(userId, "Ольга", "olga@new.com");
        UserDto expectedDto = mock(UserDto.class);

        try (MockedStatic<UserDtoMapper> mapper = Mockito.mockStatic(UserDtoMapper.class)) {
            mapper.when(() -> UserDtoMapper.updateUserFields(existing, updateDto)).thenReturn(updated);
            when(userRepository.save(updated)).thenReturn(saved);
            mapper.when(() -> UserDtoMapper.toDto(saved)).thenReturn(expectedDto);

            UserDto result = service.updateUser(userId, updateDto);

            assertSame(expectedDto, result);
            verify(userRepository).findById(userId);
            verify(userRepository).existsByEmail("olga@new.com");
            verify(userRepository).save(updated);
        }
    }

    @Test
    void getUserDtoById_success() throws NotFoundException {
        UserService service = new UserService(userRepository);

        int userId = 5;
        User user = sampleUserWithId(userId, "Сергей", "s@ex.com");
        UserDto dto = mock(UserDto.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        try (MockedStatic<UserDtoMapper> mapper = Mockito.mockStatic(UserDtoMapper.class)) {
            mapper.when(() -> UserDtoMapper.toDto(user)).thenReturn(dto);

            UserDto result = service.getUserDtoById(userId);

            assertSame(dto, result);
            verify(userRepository).findById(userId);
        }
    }

    @Test
    void deleteUser_notFound() {
        UserService service = new UserService(userRepository);

        int userId = 7;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.deleteUser(userId));

        verify(userRepository).existsById(userId);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUser_success_deletesFoundUser() throws NotFoundException {
        UserService service = new UserService(userRepository);

        int userId = 8;
        User user = sampleUserWithId(userId, "Ксения", "ks@ex.com");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        service.deleteUser(userId);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).delete(captor.capture());
        assertEquals(userId, captor.getValue().getId());
    }

    @Test
    void isUserExists() {
        UserService service = new UserService(userRepository);

        when(userRepository.existsById(10)).thenReturn(true);
        when(userRepository.existsById(11)).thenReturn(false);

        assertTrue(service.isUserExists(10));
        assertFalse(service.isUserExists(11));

        verify(userRepository).existsById(10);
        verify(userRepository).existsById(11);
    }
}