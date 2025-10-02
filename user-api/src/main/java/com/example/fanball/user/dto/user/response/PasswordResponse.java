package com.example.fanball.user.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PasswordResponse {
    private Integer resetCode;
    private LocalDateTime resetCodeExpireAt;
}
