package com.dmu.stock.controller;

import com.dmu.stock.common.ApiResponse;
import com.dmu.stock.common.SuccessType;
import com.dmu.stock.dto.SignupReqDto;
import com.dmu.stock.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signUpService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupReqDto request){
        signUpService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.SIGNUP_SUCCESS));
    }
}
