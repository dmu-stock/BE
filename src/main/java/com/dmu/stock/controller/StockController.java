package com.dmu.stock.controller;

import com.dmu.stock.client.hantu.HantuDto;
import com.dmu.stock.client.naver.NaverNewsResponseDto;
import com.dmu.stock.common.ApiResponse;
import com.dmu.stock.common.SuccessType;
import com.dmu.stock.dto.NodeStockRequestDto;
import com.dmu.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * 단순 주식 가격 및 정보 반환(한투 API)
     * @param stockCode
     * @return
     */
//    @GetMapping("/{stockCode}")
//    public ResponseEntity<ApiResponse<HantuDto.PriceResponse>> getStockInfo(@PathVariable String stockCode){
//        HantuDto.PriceResponse stockInfo = stockService.getStockInfo(stockCode);
//        return ResponseEntity.ok(ApiResponse.success(SuccessType.INQUERY_SUCCESS,stockInfo));
//    }
    /**
     * FastAPI에게 주식 가격 추이 분석 요청
     * @param stockCode
     * @return
     */
    @GetMapping("/analyze")
    public Mono<ResponseEntity<ApiResponse<String>>> getStockAnalysis(@PathVariable String stockCode){
        return stockService.getStockAnalysis(stockCode) // Mono<String>이 넘어옴
                .map(summary -> ResponseEntity.ok(
                        ApiResponse.success(SuccessType.INQUERY_SUCCESS, summary)
                ));
    }

}
