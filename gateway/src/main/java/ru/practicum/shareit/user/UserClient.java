package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> addUser(UserCreateDto userCreateDto) {
        return post("", userCreateDto);
    }

    public ResponseEntity<Object> updateUser(Integer userId, UserUpdateDto userUpdateDto) {
        return patch("/" + userId, userUpdateDto);
    }

    public ResponseEntity<Object> getUserDtoById(Integer userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> deleteUser(Integer userId) {
        return delete("/" + userId);
    }
}
