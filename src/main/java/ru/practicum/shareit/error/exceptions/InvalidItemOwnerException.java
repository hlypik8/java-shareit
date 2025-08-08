package ru.practicum.shareit.error.exceptions;

public class InvalidItemOwnerException extends RuntimeException {
    public InvalidItemOwnerException(String message) {
        super(message);
    }
}
