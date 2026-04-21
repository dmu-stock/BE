package com.dmu.stock.controller;

import com.dmu.stock.client.hantu.HantuDto;
import com.dmu.stock.common.ApiResponse;
import com.dmu.stock.common.SuccessType;
import com.dmu.stock.dto.StockRequestDto;
import com.dmu.stock.dto.StockResDto;
import com.dmu.stock.service.MemberStockService;
import com.dmu.stock.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/members/stocks")
@RequiredArgsConstructor
public class MemberStockController {
    private final MemberStockService memberStockService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<StockResDto>> saveMemberStock(@Valid @RequestBody StockRequestDto requestDto){
        StockResDto stockResponseDto = memberStockService.saveMemberStock(requestDto);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.INQUERY_SUCCESS,stockResponseDto));
    }
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<List<StockResDto>>> getMemberStock(@PathVariable String email){
        List<StockResDto> getStockList = memberStockService.getMemberStock(email);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.INQUERY_SUCCESS,getStockList));
    }
    @GetMapping("/analyze/{memberId}")
    public Mono<ResponseEntity<ApiResponse<String>>> getMyStockAnalysis(HttpServletRequest request){
        String token = jwtUtil.extractToken(request);
        String email = jwtUtil.validateAccessToken(token).getSubject();
        return memberStockService.getMyStockAnalysis(email) // Mono<String>이 넘어옴
                .map(summary -> ResponseEntity.ok(
                        ApiResponse.success(SuccessType.INQUERY_SUCCESS, summary)
                ));
    }
}
