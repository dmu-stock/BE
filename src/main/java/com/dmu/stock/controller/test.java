package com.dmu.stock.controller;

import com.dmu.stock.common.ApiResponse;
import com.dmu.stock.common.SuccessType;
import com.dmu.stock.dto.testDto;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class test {

    @PostMapping("/test")
    public ResponseEntity<ApiResponse<?>> test(@RequestBody testDto dto){
        System.out.println("주식 = " + dto.getStock() + " 가격 = " + dto.getPrice());

        return ResponseEntity.ok(ApiResponse.success(SuccessType.INQUERY_SUCCESS,dto));
    }
}
