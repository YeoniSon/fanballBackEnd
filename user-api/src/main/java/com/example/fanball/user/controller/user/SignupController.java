package com.example.fanball.user.controller.user;

import com.example.fanball.user.application.SignupApplication;
import com.example.fanball.user.domain.SignupForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignupController {
    private final SignupApplication signupApplication;

    @PostMapping("")
    public ResponseEntity<String> userSignUp(@RequestBody SignupForm form) {
        return ResponseEntity.ok(signupApplication.userSignUp(form));

    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(signupApplication.isExistNickname(nickname));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(String email, String code) {
        signupApplication.userVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
