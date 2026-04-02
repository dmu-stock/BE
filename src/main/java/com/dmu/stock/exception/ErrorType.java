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
    INVALID_PASSWORD("IP401", HttpStatus.UNAUTHORIZED, "패스워드가 일치하지 않습니다."),
    USED_USER("UU409", HttpStatus.CONFLICT, "사용중인 이메일입니다."),
    INVALID_ACCESS_TOKEN("AT401", HttpStatus.UNAUTHORIZED, "유효하지 않은 Access Token입니다."),
    INVALID_REFRESH_TOKEN("RT401", HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),

    INVALID_UUID("IU404", HttpStatus.NOT_FOUND, "이메일 인증에 실패하였습니다. 다시 인증해 주시기 바랍니다."),

    UNSET_PROPERTY("V400", HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다.");


    private final String code;
    private final HttpStatus status;
    private final String desc;
}
