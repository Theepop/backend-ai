package com.example.backendai.repository;

import com.example.backendai.model.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByMemberCode(String memberCode);
    User save(User user);
    void adjustPoints(Long userId, int delta);
}
