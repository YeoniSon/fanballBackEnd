package com.example.fanball.user.sevice.user;

import com.example.fanball.user.dto.user.UserDto;
import com.example.fanball.user.dto.user.request.FindEmailRequest;
import com.example.fanball.user.dto.user.request.UserRequestDto;
import com.example.fanball.user.entity.User;
import com.example.fanball.user.exception.UserException;
import com.example.fanball.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword())
                        && user.isVerified()
                        && (user.getBlocked() == null || !user.getBlocked()))
                .findFirst();
    }

    public Optional<String> findEmail(FindEmailRequest request) {
        User user = userRepository.findByNameAndPhone(request.getName(), request.getPhone())
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        String email = user.getEmail();
        return email == null ? Optional.empty() : Optional.of(email);
    }

    @Transactional
    public UserDto updateProfile(Long userId, UserRequestDto req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        boolean hasChanges = false;

        if (req.getNickname() != null && !req.getNickname().isBlank()
                && !Objects.equals(user.getNickname(), req.getNickname())) {
            user.setNickname(req.getNickname());
            hasChanges = true;
        }

        if (req.getFavoriteTeam() != null && !req.getFavoriteTeam().isBlank()
                && !Objects.equals(user.getFavoriteTeam(), req.getFavoriteTeam())) {
            user.setFavoriteTeam(req.getFavoriteTeam());
            hasChanges = true;
        }

        if (hasChanges) {
            userRepository.save(user);
        }

        return UserDto.from(user);
    }

    @Transactional
    public void updateReputation(Long userId, Double reportScore) {
        double sanitizedReport = reportScore == null || reportScore < 0.0 ? 0.0 : reportScore;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));
        double currentRate = user.getResponseRate() == null ? 0.0 : user.getResponseRate();
        double newRep = (currentRate * 0.8) - (sanitizedReport * 0.2);
        user.setReputation(newRep);
        userRepository.save(user);
    }

    @Transactional
    public void updateResponseRate(Long userId, Long respondedCount, Long requestCount) {
        long responded = respondedCount == null || respondedCount < 0 ? 0 : respondedCount;
        long requests = requestCount == null || requestCount < 0 ? 0 : requestCount;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));
        double rate = requests == 0 ? 0.0 : (responded * 100.0) / requests;
        user.setResponseRate(rate);
        userRepository.save(user);
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));
        user.setBlocked(true);
        userRepository.save(user);
    }
}
