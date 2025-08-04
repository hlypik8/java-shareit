package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 0;

    @Override
    public User addUser(User user) {
        int id = getNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public boolean isEmailExists(String email) {
        return users.values().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }

    private Integer getNextId() {
        return nextId++;
    }
}
