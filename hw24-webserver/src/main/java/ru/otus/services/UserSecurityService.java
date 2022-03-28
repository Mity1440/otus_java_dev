package ru.otus.services;

import ru.otus.services.model.User;

import java.util.Optional;

public interface UserSecurityService {
    Optional<User> findRandomUser();
    Optional<User> findByLogin(String login);
}