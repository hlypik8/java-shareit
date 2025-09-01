package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDtoMapperTest {

    @Test
    void toDtoTest() {
        User user = new User(10, "John Doe", "john.doe@example.com");

        UserDto dto = UserDtoMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(10, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john.doe@example.com", dto.getEmail());
    }

    @Test
    void toUserTest() {
        UserCreateDto createDto = Mockito.mock(UserCreateDto.class);
        when(createDto.getName()).thenReturn("Alice");
        when(createDto.getEmail()).thenReturn("alice@example.com");

        User user = UserDtoMapper.toUser(createDto);

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());

        verify(createDto, times(1)).getName();
        verify(createDto, times(1)).getEmail();
    }

    @Test
    void updateUserFields_ShouldUpdateNameOnly() {
        User user = new User(1, "OldName", "old@example.com");

        UserUpdateDto updateDto = mock(UserUpdateDto.class);
        when(updateDto.hasName()).thenReturn(true);
        when(updateDto.getName()).thenReturn("NewName");
        when(updateDto.hasEmail()).thenReturn(false);

        User result = UserDtoMapper.updateUserFields(user, updateDto);

        assertSame(user, result);
        assertEquals("NewName", user.getName());
        assertEquals("old@example.com", user.getEmail());

        verify(updateDto, times(1)).hasName();
        verify(updateDto, times(1)).getName();
        verify(updateDto, times(1)).hasEmail();
        verify(updateDto, never()).getEmail();
    }

    @Test
    void updateUserFields_ShouldUpdateEmailOnly() {
        User user = new User(2, "Name", "old@example.com");

        UserUpdateDto updateDto = mock(UserUpdateDto.class);
        when(updateDto.hasName()).thenReturn(false);
        when(updateDto.hasEmail()).thenReturn(true);
        when(updateDto.getEmail()).thenReturn("new@example.com");

        User result = UserDtoMapper.updateUserFields(user, updateDto);

        assertSame(user, result);
        assertEquals("Name", user.getName());
        assertEquals("new@example.com", user.getEmail());

        verify(updateDto, times(1)).hasName();
        verify(updateDto, times(1)).hasEmail();
        verify(updateDto, times(1)).getEmail();
        verify(updateDto, never()).getName();
    }

    @Test
    void updateUserFields_ShouldUpdateBoth() {
        User user = new User(3, "Old", "old@example.com");

        UserUpdateDto updateDto = mock(UserUpdateDto.class);
        when(updateDto.hasName()).thenReturn(true);
        when(updateDto.getName()).thenReturn("New");
        when(updateDto.hasEmail()).thenReturn(true);
        when(updateDto.getEmail()).thenReturn("new@example.com");

        User result = UserDtoMapper.updateUserFields(user, updateDto);

        assertSame(user, result);
        assertEquals("New", user.getName());
        assertEquals("new@example.com", user.getEmail());

        verify(updateDto, times(1)).hasName();
        verify(updateDto, times(1)).getName();
        verify(updateDto, times(1)).hasEmail();
        verify(updateDto, times(1)).getEmail();
    }

    @Test
    void updateUserFields_ShouldChangeNothing() {
        User user = new User(4, "Same", "same@example.com");

        UserUpdateDto updateDto = mock(UserUpdateDto.class);
        when(updateDto.hasName()).thenReturn(false);
        when(updateDto.hasEmail()).thenReturn(false);

        User result = UserDtoMapper.updateUserFields(user, updateDto);

        assertSame(user, result);
        assertEquals("Same", user.getName());
        assertEquals("same@example.com", user.getEmail());

        verify(updateDto, times(1)).hasName();
        verify(updateDto, times(1)).hasEmail();
        verify(updateDto, never()).getName();
        verify(updateDto, never()).getEmail();
    }
}