package com.irctc.service;

import com.irctc.enums.Role;
import com.irctc.model.User;

import java.util.Optional;

/**
 * Service interface for user registration, authentication, and profile management.
 */
public interface UserService {

    User register(String username, String password, String fullName, String email, String phone, Role role);

    User login(String username, String password);

    Optional<User> getProfile(Long userId);
}
