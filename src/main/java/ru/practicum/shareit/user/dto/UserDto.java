package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {

    private Integer id;
    private String name;
    private String email;

}
