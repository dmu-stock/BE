package com.dmu.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResDto {
    private String accessToken;
    private String refreshToken;
}
