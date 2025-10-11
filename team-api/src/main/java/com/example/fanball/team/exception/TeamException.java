package com.example.fanball.team.exception;

import lombok.Getter;

@Getter
public class TeamException extends RuntimeException{
    private final ErrorCode errorCode;

    public TeamException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }
}
