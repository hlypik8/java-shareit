package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.error.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto mockUserDto(Integer id, String name, String email) {
        UserDto dto = Mockito.mock(UserDto.class);
        when(dto.getId()).thenReturn(id);
        when(dto.getName()).thenReturn(name);
        when(dto.getEmail()).thenReturn(email);
        return dto;
    }

    @Test
    @DisplayName("POST /users - success")
    void addUser_success() throws Exception {
        String requestJson = "{\n\"name\":\"Иван\",\"email\":\"ivan@example.com\"\n}";

        UserDto returned = mockUserDto(1, "Иван", "ivan@example.com");

        when(userService.addUser(any(UserCreateDto.class))).thenReturn(returned);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService, times(1)).addUser(any(UserCreateDto.class));
    }

    @Test
    @DisplayName("POST /users - NotUniqueEmailException -> 409 with error body")
    void addUser_emailConflict_returns409() throws Exception {
        String requestJson = "{\n\"name\":\"Иван\",\"email\":\"conflict@example.com\"\n}";

        when(userService.addUser(any(UserCreateDto.class)))
                .thenThrow(new NotUniqueEmailException("Пользователь с таким email-адресом уже существует"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict());

        verify(userService, times(1)).addUser(any(UserCreateDto.class));
    }

    @Test
    @DisplayName("PATCH /users/{id} - success")
    void updateUser_success() throws Exception {
        int userId = 5;
        String requestJson = "{\n\"name\":\"Ольга\",\"email\":\"olga@new.com\"\n}";

        UserDto returned = mockUserDto(userId, "Ольга", "olga@new.com");

        when(userService.updateUser(eq(userId), any(UserUpdateDto.class))).thenReturn(returned);

        mvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Ольга"))
                .andExpect(jsonPath("$.email").value("olga@new.com"));

        verify(userService, times(1)).updateUser(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    @DisplayName("GET /users/{id} - success")
    void getUserById_success() throws Exception {
        int userId = 3;
        UserDto returned = mockUserDto(userId, "Сергей", "sergey@example.com");

        when(userService.getUserDtoById(userId)).thenReturn(returned);

        mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Сергей"))
                .andExpect(jsonPath("$.email").value("sergey@example.com"));

        verify(userService, times(1)).getUserDtoById(userId);
    }

    @Test
    @DisplayName("GET /users/{id} - NotFoundException -> 404")
    void getUserById_notFound_returns404() throws Exception {
        int userId = 99;
        when(userService.getUserDtoById(userId)).thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserDtoById(userId);
    }

    @Test
    @DisplayName("DELETE /users/{id} - success")
    void deleteUser_success() throws Exception {
        int userId = 7;
        // deleteUser returns void; by default mock does nothing
        doNothing().when(userService).deleteUser(userId);

        mvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("DELETE /users/{id} - NotFoundException -> 404")
    void deleteUser_notFound_returns404() throws Exception {
        int userId = 100;
        doThrow(new NotFoundException("Пользователь не найден")).when(userService).deleteUser(userId);

        mvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(userId);
    }
}