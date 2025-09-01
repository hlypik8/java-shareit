package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemUpdateDtoTest {

    @Test
    void hasName_whenNameIsNull_returnsFalse() {
        ItemUpdateDto dto = new ItemUpdateDto(null, "desc", true);
        assertFalse(dto.hasName());
    }

    @Test
    void hasName_whenNameIsEmpty_returnsFalse() {
        ItemUpdateDto dto = new ItemUpdateDto("", "desc", true);
        assertFalse(dto.hasName());
    }

    @Test
    void hasName_whenNameIsNotEmpty_returnsTrue() {
        ItemUpdateDto dto = new ItemUpdateDto("Drill", "desc", true);
        assertTrue(dto.hasName());
    }

    @Test
    void hasDescription_whenDescriptionIsNull_returnsFalse() {
        ItemUpdateDto dto = new ItemUpdateDto("Hammer", null, true);
        assertFalse(dto.hasDescription());
    }

    @Test
    void hasDescription_whenDescriptionIsEmpty_returnsFalse() {
        ItemUpdateDto dto = new ItemUpdateDto("Hammer", "", true);
        assertFalse(dto.hasDescription());
    }

    @Test
    void hasDescription_whenDescriptionIsNotEmpty_returnsTrue() {
        ItemUpdateDto dto = new ItemUpdateDto("Hammer", "Steel", true);
        assertTrue(dto.hasDescription());
    }

    @Test
    void hasAvailable_whenAvailableIsNull_returnsFalse() {
        ItemUpdateDto dto = new ItemUpdateDto("Hammer", "Steel", null);
        assertFalse(dto.hasAvailable());
    }

    @Test
    void hasAvailable_whenAvailableIsTrue_returnsTrue() {
        ItemUpdateDto dto = new ItemUpdateDto("Hammer", "Steel", true);
        assertTrue(dto.hasAvailable());
    }

    @Test
    void hasAvailable_whenAvailableIsFalse_returnsTrue() {
        ItemUpdateDto dto = new ItemUpdateDto("Hammer", "Steel", false);
        assertTrue(dto.hasAvailable(), "hasAvailable должен вернуть true, даже если available == false");
    }

}