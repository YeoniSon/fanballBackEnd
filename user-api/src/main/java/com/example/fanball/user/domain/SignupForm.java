package com.example.fanball.user.domain;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupForm {

    private String email;
    private String name;
    private String nickName;
    private String password;
    private String phone;
    private LocalDate birth;
}
