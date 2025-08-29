package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    private String name;

    @Email(message = "Неверный формат email")
    private String email;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasEmail() {
        return email != null;
    }
}
