package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    private String name;

    private String email;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasEmail() {
        return email != null;
    }
}
