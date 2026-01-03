// src/main/java/com/example/nothingdoing/repository/UserRepository.java
package com.example.nothingdoing.repository;

import com.example.nothingdoing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}