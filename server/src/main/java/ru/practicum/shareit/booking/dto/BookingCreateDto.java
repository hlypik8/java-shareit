package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class BookingCreateDto {

    Integer itemId;

    LocalDateTime start;

    LocalDateTime end;
}
