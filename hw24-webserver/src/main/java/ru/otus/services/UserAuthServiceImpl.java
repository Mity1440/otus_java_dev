package ru.otus.services;

public class UserAuthServiceImpl implements UserAuthService {

    private final UserSecurityService userSecurityService;

    public UserAuthServiceImpl(UserSecurityService userSecurityService) {
        this.userSecurityService = userSecurityService;
    }

    @Override
    public boolean authenticate(String login, String password) {
        return userSecurityService.findByLogin(login)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

}
