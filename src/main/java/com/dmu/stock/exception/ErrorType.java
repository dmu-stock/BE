package com.dmu.stock.exception;

import com.dmu.stock.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType implements ErrorCode {
    MEMBER_NOT_FOUND("M404", HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    CHECK_MEMBER_FAIL("CM404", HttpStatus.NOT_FOUND, "이름과 이메일을 확인해 주세요."),
    INVALID_ACCESS_TOKEN("AT401", HttpStatus.UNAUTHORIZED, "유효하지 않은 Access Token입니다."),
    INVALID_REFRESH_TOKEN("RT401", HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
    UNSET_PROPERTY("V400", HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    DATABASE_ERROR("V500", HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 서버 오류가 발생했습니다.");


    private final String code;
    private final HttpStatus status;
    private final String desc;
}
