package ru.otus.services;

import ru.otus.services.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class InMemoryUserSecurityService implements UserSecurityService {

    private final Map<Long, User> users;

    public InMemoryUserSecurityService() {
        users = new HashMap<>();
        users.put(1L, new User( "user1", "11111"));
        users.put(2L, new User( "user2", "11111"));

    }

    @Override
    public Optional<User> findRandomUser() {
        Random r = new Random();
        return users.values().stream().skip(r.nextInt(users.size() - 1)).findFirst();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.values().stream().filter(v -> v.getLogin().equals(login)).findFirst();
    }
}
