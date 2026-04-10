package com.dmu.stock.controller;

import com.dmu.stock.client.hantu.HantuDto;
import com.dmu.stock.common.ApiResponse;
import com.dmu.stock.common.SuccessType;
import com.dmu.stock.dto.NodeStockRequestDto;
import com.dmu.stock.dto.StockRequestDto;
import com.dmu.stock.dto.StockResponseDto;
import com.dmu.stock.service.MemberStockService;
import com.dmu.stock.service.StockService;
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

    @PostMapping
    public ResponseEntity<ApiResponse<StockResponseDto>> saveMemberStock(@Valid @RequestBody StockRequestDto requestDto){
        StockResponseDto stockResponseDto = memberStockService.saveMemberStock(requestDto);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.INQUERY_SUCCESS,stockResponseDto));
    }
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<List<StockResponseDto>>> getMemberStock(@PathVariable String memberId){
        List<StockResponseDto> getStockList = memberStockService.getMemberStock(memberId);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.INQUERY_SUCCESS,getStockList));
    }
    @GetMapping("/analyze/{memberId}")
    public Mono<ResponseEntity<ApiResponse<String>>> getMyStockAnalysis(@PathVariable String memberId){
        return memberStockService.getMyStockAnalysis(memberId) // Mono<String>이 넘어옴
                .map(summary -> ResponseEntity.ok(
                        ApiResponse.success(SuccessType.INQUERY_SUCCESS, summary)
                ));
    }
}
