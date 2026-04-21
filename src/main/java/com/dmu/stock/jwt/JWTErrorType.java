package com.dmu.stock.jwt;

import com.dmu.stock.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JWTErrorType implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED,"존재하지 않는 사용자입니다."),
    AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED,"인증 오류가 발생하였습니다."),
    INVALID_TOKEN_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    REFRESH_TOKEN_NONE(HttpStatus.UNAUTHORIZED, "토큰을 확인할 수 없습니다."),

    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "로그인이 만료되었습니다. 다시 로그인 해주세요."),

    BLACKLISTED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "블랙리스트에 등록된 토큰입니다.");

    private final HttpStatus status;
    private final String desc;
}
