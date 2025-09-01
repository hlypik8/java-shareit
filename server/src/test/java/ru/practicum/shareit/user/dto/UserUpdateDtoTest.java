package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserUpdateDtoTest {
    @Test
    void noArgsConstructor_ShouldInitializeWithNulls() {
        UserUpdateDto dto = new UserUpdateDto();

        assertNull(dto.getName(), "name должен быть null у no-arg конструктора");
        assertNull(dto.getEmail(), "email должен быть null у no-arg конструктора");
        assertFalse(dto.hasName(), "hasName() должен вернуть false при null name");
        assertFalse(dto.hasEmail(), "hasEmail() должен вернуть false при null email");
    }

    @Test
    void allArgsConstructor_ShouldSetFields() {
        UserUpdateDto dto = new UserUpdateDto("John", "john@example.com");

        assertEquals("John", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
        assertTrue(dto.hasName());
        assertTrue(dto.hasEmail());
    }

    @Test
    void hasName_ShouldReturnTrueForEmptyString() {
        UserUpdateDto dto = new UserUpdateDto("", null);

        assertEquals("", dto.getName());
        assertTrue(dto.hasName(), "hasName() должен вернуть true для пустой строки (не null)");
        assertFalse(dto.hasEmail());
    }

    @Test
    void hasEmail_ShouldReturnFalseWhenNull() {
        UserUpdateDto dto = new UserUpdateDto("Alice", null);

        assertTrue(dto.hasName());
        assertFalse(dto.hasEmail());
    }

    @Test
    void hasNameAndHasEmail_BothNullAndNonNullCombinations() {
        UserUpdateDto bothNull = new UserUpdateDto(null, null);
        assertFalse(bothNull.hasName());
        assertFalse(bothNull.hasEmail());

        UserUpdateDto onlyEmail = new UserUpdateDto(null, "a@b.com");
        assertFalse(onlyEmail.hasName());
        assertTrue(onlyEmail.hasEmail());

        UserUpdateDto onlyName = new UserUpdateDto("Name", null);
        assertTrue(onlyName.hasName());
        assertFalse(onlyName.hasEmail());
    }

}