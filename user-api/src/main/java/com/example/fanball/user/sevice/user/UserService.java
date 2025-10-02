package com.example.fanball.user.sevice.user;

import com.example.fanball.user.dto.user.request.FindEmailRequest;
import com.example.fanball.user.entity.User;
import com.example.fanball.user.exception.UserException;
import com.example.fanball.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.fanball.user.exception.ErrorCode.NOT_FOUND_USER;

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

    public Optional<String> findEmail(FindEmailRequest request) {
        User user = userRepository.findByNameAndPhone(request.getName(), request.getPhone())
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        String email = user.getEmail();
        return email == null ? Optional.empty() : Optional.of(email);
    }
}
