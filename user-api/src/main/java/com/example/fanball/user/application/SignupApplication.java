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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class SignupApplication {
    private final UserSignupService userSignupService;
    private final MailComponent mailComponent;
    private final TemplateEngine templateEngine;

    public void userVerify(String email, String code) {
        userSignupService.verifyEmail(email, code);
    }

    public NicknameCheckResponseDto isExistNickname(NicknameCheckRequsetDto requestDto) {
        boolean exists = userSignupService.isNicknameExist(requestDto.getNickname());

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

            // build verification link and render template
            String verifyLink = "http://localhost:8081/signup/verify?email="
                    + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)
                    + "&code=" + code;
            Context ctx = new Context();
            ctx.setVariable("name", user.getName());
            ctx.setVariable("verifyLink", verifyLink);
            ctx.setVariable("expireMinutes", 10);
            String html = templateEngine.process("SignupVerifyMail", ctx);

            mailComponent.sendMail(user.getEmail(), subject, html);

            userSignupService.changeUserValidateEmail(user.getId(), code);
            return "회원 가입에 성공하였습니다.";
        }
    }

    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }


}
