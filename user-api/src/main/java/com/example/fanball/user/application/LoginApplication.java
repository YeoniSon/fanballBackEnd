package com.example.fanball.user.application;

import com.example.common.config.JwtTokenProvider;
import com.example.common.domain.UserType;
import com.example.fanball.user.domain.LoginForm;
import com.example.fanball.user.entity.User;
import com.example.fanball.user.exception.UserException;
import com.example.fanball.user.sevice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.fanball.user.exception.ErrorCode.LOGIN_CHECK_FAIL;

@Service
@RequiredArgsConstructor
public class LoginApplication {
    private final UserService userService;
    private final JwtTokenProvider provider;

    public String userLoginToken(LoginForm form) {
        // 1. 로그인 가능 여부
        User user = userService.findValidUser(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new UserException(LOGIN_CHECK_FAIL));

        // 2. 토큰을 발행

        // 3. 토큰을 response한다.
        return provider.createToken(
                user.getEmail(),
                user.getId(),
                UserType.valueOf(user.getRole().toUpperCase())
        );
    }
}
