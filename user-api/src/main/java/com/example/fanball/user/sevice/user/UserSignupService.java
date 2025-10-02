package com.example.fanball.user.sevice.user;

import com.example.fanball.user.domain.SignupForm;
import com.example.fanball.user.entity.User;
import com.example.fanball.user.exception.UserException;
import com.example.fanball.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static com.example.fanball.user.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserSignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signUp(SignupForm form) {
        User user = User.from(form);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email.toLowerCase(Locale.ROOT))
                .isPresent();
    }

    public boolean isNicknameExist(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if (user.isVerified()) {
            throw new UserException(ALREADY_VERIFY);
        } else if (!user.getVerificationCode().equals(code)) {
            throw new UserException(WRONG_VERIFICATION);
        } else if (user.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new UserException(EXPIRE_CODE);
        }
        user.setVerified(true);
    }

    @Transactional
    public LocalDateTime changeUserValidateEmail(Long userId, String verificationCode) {
        Optional<User> customerOptional = userRepository.findById(userId);

        if (customerOptional.isPresent()) {
            User user = customerOptional.get();
            user.setVerificationCode(verificationCode);
            user.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));

            return user.getVerifyExpiredAt();
        }

        throw new UserException(NOT_FOUND_USER);
    }
}
