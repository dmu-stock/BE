package com.dmu.stock.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessType {
    INQUERY_SUCCESS("S200", HttpStatus.OK,"조회에 성공하였습니다."),
    CREATED("C201", HttpStatus.CREATED,"작업이 완료되었습니다."),
    LOGIN_SUCCESS("L200", HttpStatus.OK, "로그인 되었습니다."),
    REGISTER_SUCCESS("R200", HttpStatus.OK, "사용 가능한 이메일입니다."),
    TOKEN_REFRESH_SUCCESS("TR200",HttpStatus.OK,"토큰이 성공적으로 발급 되었습니다.");


    private final String code;
    private final HttpStatus status;
    private final String desc;
}
