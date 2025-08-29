package ru.practicum.shareit.booking.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookingCreateDto {

    Integer itemId;

    LocalDateTime start;

    LocalDateTime end;
}
