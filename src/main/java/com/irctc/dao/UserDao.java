package com.irctc.dao;

import com.irctc.model.User;

import java.util.Optional;

/**
 * Data Access Object interface for {@link User} entity persistence operations.
 */
public interface UserDao {

    Optional<User> findById(Long userId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User create(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
