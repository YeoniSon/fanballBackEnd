package com.example.fanball.user.controller.user;

import com.example.common.config.JwtTokenProvider;
import com.example.common.domain.UserVo;
import com.example.fanball.user.dto.user.UserDto;
import com.example.fanball.user.dto.user.request.FindEmailRequest;
import com.example.fanball.user.dto.user.request.PasswordRequest;
import com.example.fanball.user.dto.user.request.UserRequestDto;
import com.example.fanball.user.entity.User;
import com.example.fanball.user.exception.UserException;
import com.example.fanball.user.sevice.user.PasswordService;
import com.example.fanball.user.sevice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.fanball.user.exception.ErrorCode.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordService passwordService;

//    정보 불러오기
    @GetMapping("/getInfo")
    public ResponseEntity<?> getInfo(
            @RequestHeader(name = "X-AUTH-TOKEN") String token
    ) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UserException(INVALID_TOKEN);
        }

        UserVo vo;
        try {
            vo = jwtTokenProvider.getUserVo(token);
        } catch (Exception e) {
            throw new UserException(INVALID_TOKEN_PAYLOAD);
        }

        User user = userService.findByIdAndEmail(vo.getId(), vo.getEmail())
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        return ResponseEntity.ok(UserDto.from(user));
    }

//    정보 변경
    @PatchMapping(value = "/changeInfo")
    public ResponseEntity<?> changeInfo(
            @RequestHeader("X-AUTH-TOKEN") String token,
            @RequestBody @Valid UserRequestDto req
    ) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        UserVo vo;
        try {
            vo = jwtTokenProvider.getUserVo(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token payload");
        }

        return ResponseEntity.ok(userService.updateProfile(vo.getId(), req));
    }

    @PatchMapping("/reputation")
    public ResponseEntity<Void> updateReputation(
            @RequestHeader("X-AUTH-TOKEN") String token,
            @RequestBody @Valid UserRequestDto.ReputationRequest req
    ) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UserException(INVALID_TOKEN);
        }
        UserVo vo = jwtTokenProvider.getUserVo(token);
        userService.updateReputation(vo.getId(), req.getReportScore());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/responseRate")
    public ResponseEntity<Void> updateResponseRate(
            @RequestHeader("X-AUTH-TOKEN") String token,
            @RequestBody @Valid UserRequestDto.ResponseRateRequest req
    ) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UserException(INVALID_TOKEN);
        }
        UserVo vo = jwtTokenProvider.getUserVo(token);
        userService.updateResponseRate(vo.getId(), req.getRespondedCount(), req.getRequestCount());
        return ResponseEntity.ok().build();
    }

    // 이메일 찾기
    @PostMapping("/findEmail")
    public ResponseEntity<?> findEmail(
            @RequestBody @Valid FindEmailRequest request
    ) {
        return ResponseEntity.ok(userService.findEmail(request));
    }

    //비밀번호 찾기는 변경시 인증 번호를 메일로 보내고 이를 입력하면 비밀번호 변경
    @PostMapping("/findPassword/requestReset")
    public ResponseEntity<Void> resetRequest(
            @RequestBody @Valid PasswordRequest.FindPasswordRequest req
    ) {
        passwordService.requestResetPassword(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/findPassword/confirmReset")
    public ResponseEntity<String> confirmReset(
            @RequestBody @Valid PasswordRequest.ResetCodeRequest req
    ) {
        String verifiedToken = passwordService.confirmReset(req);
        return ResponseEntity.ok(verifiedToken);
    }

    @PatchMapping("/findPassword/changePassword")
    public ResponseEntity<String> findPasswordChange(
            @RequestHeader(name = "Verified-Token") String token,
            @RequestBody @Valid PasswordRequest.ChangePasswordRequest req
    ) {
        passwordService.changePassword(token ,req);
        return ResponseEntity.ok().build();
    }

    // 탈퇴하기
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @RequestHeader(name = "X-AUTH-TOKEN") String token
    ) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UserException(INVALID_TOKEN);
        }
        UserVo vo;
        try {
            vo = jwtTokenProvider.getUserVo(token);
        } catch (Exception e) {
            throw new UserException(INVALID_TOKEN_PAYLOAD);
        }
        userService.withdraw(vo.getId());
        return ResponseEntity.noContent().build();
    }
}
