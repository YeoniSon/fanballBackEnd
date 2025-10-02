package com.example.fanball.user.application;

import com.example.fanball.user.domain.SignupForm;
import com.example.fanball.user.dto.user.request.NicknameCheckRequsetDto;
import com.example.fanball.user.dto.user.response.NicknameCheckResponseDto;
import com.example.fanball.user.entity.User;
import com.example.fanball.user.exception.UserException;
import com.example.fanball.user.mail.MailComponent;
import com.example.fanball.user.sevice.user.UserSignupService;

import static com.example.fanball.user.exception.ErrorCode.ALREADY_EXIST_NICKNAME;
import static com.example.fanball.user.exception.ErrorCode.ALREADY_REGISTER_USER;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupApplication {
    private final UserSignupService userSignupService;
    private final MailComponent mailComponent;

    public void userVerify(String email, String code) {
        userSignupService.verifyEmail(email, code);
    }

    public NicknameCheckResponseDto isExistNickname(NicknameCheckRequsetDto requsetDto) {
        boolean exists = userSignupService.isNicknameExist(requsetDto.getNickname());

        return  new NicknameCheckResponseDto(exists);
    }

    public String userSignUp(SignupForm form) {

        String subject = "FanBall 회원가입 인증 메일";

        if (userSignupService.isEmailExist(form.getEmail())) {
            //예외처리
            throw new UserException(ALREADY_REGISTER_USER);
        }else if (userSignupService.isNicknameExist(form.getNickName())) {
            throw new UserException(ALREADY_EXIST_NICKNAME);
        }else {
            // 회원가입
            User user = userSignupService.signUp(form);

            String code = getRandomCode();

            mailComponent.sendMail(user.getEmail(), subject, getMailBody(user.getEmail(), code));

            userSignupService.changeUserValidateEmail(user.getId(), code);
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
