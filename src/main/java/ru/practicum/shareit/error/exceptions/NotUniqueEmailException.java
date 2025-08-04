package ru.practicum.shareit.error.exceptions;

public class NotUniqueEmailException extends RuntimeException {

    public NotUniqueEmailException(String message) {
        super(message);
    }

}
