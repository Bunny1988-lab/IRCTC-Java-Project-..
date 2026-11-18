package com.irctc.service.impl;

import com.irctc.dao.UserDao;
import com.irctc.dao.impl.UserDaoImpl;
import com.irctc.enums.Role;
import com.irctc.exception.AuthenticationException;
import com.irctc.exception.ValidationException;
import com.irctc.model.User;
import com.irctc.service.UserService;
import com.irctc.util.PasswordUtil;

import java.util.Optional;

/**
 * Implementation of {@link UserService}.
 */
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl() {
        this(new UserDaoImpl());
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User register(String username, String password, String fullName, String email, String phone, Role role) {
        validateRegistrationInputs(username, password, fullName, email, phone);

        if (userDao.existsByUsername(username)) {
            throw new ValidationException("Username '" + username + "' is already taken. Please choose another.");
        }

        if (userDao.existsByEmail(email)) {
            throw new ValidationException("Email '" + email + "' is already registered with another account.");
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(username.trim(), hashedPassword, fullName.trim(), email.trim().toLowerCase(), phone.trim(), role != null ? role : Role.PASSENGER);

        return userDao.create(user);
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationException("Username and password must not be empty.");
        }

        User user = userDao.findByUsername(username.trim())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        return user;
    }

    @Override
    public Optional<User> getProfile(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return userDao.findById(userId);
    }

    private void validateRegistrationInputs(String username, String password, String fullName, String email, String phone) {
        if (username == null || username.trim().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long.");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Full name cannot be blank.");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new ValidationException("Please provide a valid email address.");
        }
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new ValidationException("Phone number must be exactly 10 digits.");
        }
    }
}
