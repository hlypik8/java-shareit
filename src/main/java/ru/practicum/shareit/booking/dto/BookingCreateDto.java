package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookingCreateDto {

    @NotNull(message = "ID вещи не может быть пустым")
    Integer itemId;

    @NotNull(message = "Время начала аренды не может быть пустым")
    LocalDateTime start;

    @Future(message = "Время конца аренды должно быть в будущем ")
    @NotNull(message = "Время конца аренды не может быть пустым")
    LocalDateTime end;
}
