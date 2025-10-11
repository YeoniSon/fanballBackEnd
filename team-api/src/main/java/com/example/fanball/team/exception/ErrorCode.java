package com.example.fanball.team.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    FAIL_TEAM_DATA_INPUT(HttpStatus.BAD_REQUEST, "데이터를 가져오지 못했습니다.");


    private final HttpStatus httpStatus;
    private final String detail;
}
