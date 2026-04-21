package com.dmu.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupReqDto {
    private String email;
    private String password;
    private String name;
    private String phone;
    // 필요한 경우 닉네임, 이름, 계좌 등 추가
}
