// src/main/java/com/example/nothingdoing/service/UserService.java
package com.example.nothingdoing.service;

import com.example.nothingdoing.entity.User;
import com.example.nothingdoing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }

    public boolean register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) return false;
        User user = new User(username, passwordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public void addMinutes(String username, long minutes) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setTotalMinutes(user.getTotalMinutes() + minutes);
        userRepository.save(user);
    }
}