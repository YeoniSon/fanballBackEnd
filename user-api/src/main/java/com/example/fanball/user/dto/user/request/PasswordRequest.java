package com.example.fanball.user.dto.user.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class PasswordRequest {
    @Getter
    @Setter
    public static class FindPasswordRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String name;

        @NotBlank
        private String phone;
    }

    @Getter
    @Setter
    public static class ResetCodeRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String code;
    }

    @Getter
    @Setter
    public static class ChangePasswordRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String newPassword;
    }
}
