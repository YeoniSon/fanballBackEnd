package com.example.fanball.user.sevice.user;

import com.example.fanball.user.dto.user.request.PasswordRequest;
import com.example.fanball.user.entity.User;
import com.example.fanball.user.exception.UserException;
import com.example.fanball.user.mail.MailComponent;
import com.example.fanball.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.security.SecureRandom;
import java.util.Date;

import static com.example.fanball.user.exception.ErrorCode.*;
import static com.example.fanball.user.exception.ErrorCode.NOT_FOUND_USER;
import static com.example.fanball.user.exception.ErrorCode.WRONG_VERIFICATION;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MailComponent mailComponent;
    private final TemplateEngine templateEngine;

    @Transactional
    public void requestResetPassword(PasswordRequest.FindPasswordRequest request) {
        // 사용자 확인
        User user = userRepository.findByEmailAndNameAndPhone(
                request.getEmail(), request.getName(), request.getPhone()
        ).orElseThrow(() -> new UserException(NOT_FOUND_USER));

        // 6자리 코드 발급 + 만료 기간 저장(10분)
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        user.setResetCode(code);
        user.setResetCodeExpireAt(LocalDateTime.now().plusMinutes(10));

        // 메일 전송
        String subject = "[Fanball] 비밀번호 재설정 코드";
        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("code", code);
        String html = templateEngine.process("PasswordResetCodeMail", context);
        mailComponent.sendMail(user.getEmail(), subject, html);
    }


    @Transactional(readOnly = true)
    public String confirmReset(PasswordRequest.ResetCodeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if (user.getResetCode() == null || user.getResetCodeExpireAt() == null) {
            throw new UserException(WRONG_VERIFICATION);
        } else if (!user.getResetCode().equals(request.getCode())) {
            throw new UserException(WRONG_VERIFICATION);
        } else if (user.getResetCodeExpireAt().isBefore(LocalDateTime.now())) {
            throw new UserException(EXPIRE_CODE);
        }else {
            user.setResetCode(null);
            user.setResetCodeExpireAt(null);

            long now = System.currentTimeMillis();

            return Jwts.builder()
                    .setSubject("reset")
                    .claim("purpose", "RESET")
                    .claim("email", request.getEmail())
                    .claim("nonce", request.getCode())
                    .setIssuedAt(new Date(now))
                    .signWith(SignatureAlgorithm.HS256, "seccretKey")
                    .compact();
        }
    }

    @Transactional
    public void changePassword(
            String verificationCode,
            PasswordRequest.ChangePasswordRequest request
    ) {
        var claims = Jwts.parser()
                .setSigningKey("seccretKey")
                .parseClaimsJws(verificationCode)
                .getBody();

        if (!"RESET".equals(claims.get("purpose"))) {
            throw new UserException(WRONG_VERIFICATION);
        }
        String tokenEmail = (String) claims.get("email");
        String nonce = (String) claims.get("nonce");

        if (!request.getEmail().equals(tokenEmail)) {
            throw new UserException(WRONG_VERIFICATION);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if (user.getResetCode() == null ||
        !nonce.equals(user.getResetCode()) ||
        user.getResetCodeExpireAt().isBefore(LocalDateTime.now())) {
            throw new UserException(WRONG_VERIFICATION);
        }

        // 비밀번호 변경 후 즉시 무효화
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetCode(null);
        user.setResetCodeExpireAt(null);
    }}