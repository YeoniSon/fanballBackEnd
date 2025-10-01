package com.example.fanball.user.application;

import com.example.fanball.user.domain.SignupForm;
import com.example.fanball.user.entity.User;
import com.example.fanball.user.exception.ErrorCode;
import com.example.fanball.user.exception.UserException;
import com.example.fanball.user.mail.MailComponent;
import com.example.fanball.user.mail.SendMailForm;
import com.example.fanball.user.sevice.user.SignupUserService;

import java.util.HashMap;
import java.util.Map;

import static com.example.fanball.user.exception.ErrorCode.ALREADY_EXIST_NICKNAME;
import static com.example.fanball.user.exception.ErrorCode.ALREADY_REGISTER_USER;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupApplication {
    private final SignupUserService signupUserService;
    private final MailComponent mailComponent;

    public void userVerify(String email, String code) {
        signupUserService.verifyEmail(email, code);
    }

    public Map<String, Boolean> isExistNickname(String nickname) {
        boolean exists = signupUserService.isNicknameExist(nickname);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exist", exists);

        return response;
    }

    public String userSignUp(SignupForm form) {

        String subject = "FanBall 회원가입 인증 메일";

        if (signupUserService.isEmailExist(form.getEmail())) {
            //예외처리
            throw new UserException(ALREADY_REGISTER_USER);
        }else if (signupUserService.isNicknameExist(form.getNickName())) {
            throw new UserException(ALREADY_EXIST_NICKNAME);
        }else {
            // 회원가입
            User user = signupUserService.signUp(form);

            String code = getRandomCode();

            mailComponent.sendMail(user.getEmail(), subject, getMailBody(user.getEmail(), code));

            signupUserService.changeUserValidateEmail(user.getId(), code);
            return "회원 가입에 성공하였습니다.";
        }
    }

    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getMailBody(String email, String code) {
        StringBuilder sb = new StringBuilder();
        return sb.append("안녕하세요. FanBall 입니다.\n\n")
                .append("아래 링크를 클릭하여 이메일 인증을 완료해 주세요.\n\n")
                .append("http://localhost:8081/signup/verify?email=")
                .append(email)
                .append("&code=")
                .append(code)
                .append("\n\n감사합니다.")
                .toString();
    }

}
