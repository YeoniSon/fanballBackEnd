package com.example.fanball.user.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final ErrorCode errorCode;

    public UserException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

}
