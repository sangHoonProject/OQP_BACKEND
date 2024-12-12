package com.example.oqp.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    LOGIN_FAILED(400, "로그인 실패. 아이디, 패스워드를 확인해주세요."),
    ALREADY_USED_EMAIL(400, "이미 사용된 이메일입니다."),
    ROLE_NOT_FOUND(403, "권한 정보가 없습니다."),
    REFRESH_TOKEN_EXPIRE(403, "refresh 토큰이 만료되었습니다."),
    USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),
    REFRESH_TOKEN_NOT_FOUND(404, "존재하지 않는 토큰입니다.");

    private final int status;
    private final String message;
}
