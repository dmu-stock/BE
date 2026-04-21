package com.dmu.stock.controller;

import com.dmu.stock.common.ApiResponse;
import com.dmu.stock.common.SuccessType;
import com.dmu.stock.dto.LoginReqDto;
import com.dmu.stock.dto.LoginResDto;
import com.dmu.stock.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoginResDto>> login(@RequestBody LoginReqDto dto){
        LoginResDto login = loginService.login(dto.getEmail(),dto.getPassword());
        return ResponseEntity.ok(ApiResponse.success(SuccessType.LOGIN_SUCCESS,login));

    }
}
