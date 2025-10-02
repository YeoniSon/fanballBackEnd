package com.example.fanball.user.dto.user.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class NicknameCheckRequsetDto {


    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    public NicknameCheckRequsetDto(String nickname) {
        this.nickname = nickname;
    }
}
