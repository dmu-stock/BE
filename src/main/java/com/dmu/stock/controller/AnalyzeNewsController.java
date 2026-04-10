package com.dmu.stock.controller;

import com.dmu.stock.common.ApiResponse;
import com.dmu.stock.common.SuccessType;
import com.dmu.stock.service.AnalyzeNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/news")
@RequiredArgsConstructor
public class AnalyzeNewsController {

    private final AnalyzeNewsService analyzeNewsService;



    /**
     * 주식이름으로 뉴스 수집
     * @param stockCode
     * @return
     */
    @GetMapping("/{stockCode}")
    public ResponseEntity<ApiResponse<List<String>>> getStockNews(@PathVariable List<String> stockCode){
        List<String> stockNews = analyzeNewsService.getNewsByName(stockCode);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.INQUERY_SUCCESS,stockNews));
    }


    /**
     * 검색어 자유 뉴스 수집
     * @param query
     * @param display
     * @return
     */
    @GetMapping("/search")
    public Mono<ResponseEntity<ApiResponse<String>>> getStockNews(@RequestParam String query, @RequestParam int display){
        return analyzeNewsService.getNews(query, display)
                .map(summary -> ResponseEntity.ok(
                        ApiResponse.success(SuccessType.INQUERY_SUCCESS, summary)
                ));
    }

}
