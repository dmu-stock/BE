package com.dmu.stock.service;

import com.dmu.stock.client.fastapi.FastApiClient;
import com.dmu.stock.client.hantu.HantuClient;
import com.dmu.stock.client.hantu.HantuDto;
import com.dmu.stock.client.naver.NaverNewsClient;
import com.dmu.stock.client.naver.NaverNewsResponseDto;
import com.dmu.stock.client.naver.NewsAnalysisRequestDto;
import com.dmu.stock.config.WebClientConfig;
import com.dmu.stock.dto.NodeStockRequestDto;
import com.dmu.stock.dto.StockAnalysisRequestDto;
import com.dmu.stock.entity.enums.StockType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final HantuClient hantuClient;
    private final NaverNewsClient naverNewsClient;
    private final FastApiClient fastApiClient;

    /**
     * 단순 주식 가격 및 정보 반환(한투 API)
     * @param stockCode
     * @return
     */
    public HantuDto.PriceResponse getStockInfo(String stockCode){
        return hantuClient.getStockPrice(stockCode);
    }

    /**
     * FastAPI에게 주식 가격 추이 분석 요청
     * @param
     * @return
     */
    public Mono<String> getStockAnalysis(String stockCode){
        StockAnalysisRequestDto stockAnalysisRequestDto;

            stockAnalysisRequestDto = StockAnalysisRequestDto.builder()
                    .stockCode(stockCode)
                    .type(StockType.detectType(stockCode))
                    .build();

        return fastApiClient.analyzeStock(stockAnalysisRequestDto);
    }
}
