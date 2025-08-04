package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.annotations.UniqueEmail;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class User {

    private Integer id;
    @NotNull
    private String name;
    @Email
    @UniqueEmail
    @NotNull
    protected String email;

}
