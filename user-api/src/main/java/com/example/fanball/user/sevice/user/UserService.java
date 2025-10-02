package com.example.fanball.user.sevice.user;

import com.example.fanball.user.entity.User;
import com.example.fanball.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByIdAndEmail(Long id, String email) {
        return userRepository.findById(id)
                .stream().filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public Optional<User> findValidUser(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .stream()
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()) && user.isVerified())
                .findFirst();
    }
}
