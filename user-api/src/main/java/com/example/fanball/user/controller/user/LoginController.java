package com.example.fanball.user.controller.user;

import com.example.fanball.user.application.LoginApplication;
import com.example.fanball.user.domain.LoginForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginApplication loginApplication;

    @PostMapping("")
    public ResponseEntity<String> loginUser(@RequestBody LoginForm form) {
        return ResponseEntity.ok(loginApplication.userLoginToken(form));
    }
}
