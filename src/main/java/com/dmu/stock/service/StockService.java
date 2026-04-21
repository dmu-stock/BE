package com.dmu.stock.service;

import com.dmu.stock.client.fastapi.FastApiClient;
import com.dmu.stock.client.hantu.HantuClient;
import com.dmu.stock.client.hantu.HantuDto;
import com.dmu.stock.dto.StockAnalysisRequestDto;
import com.dmu.stock.entity.enums.StockType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final HantuClient hantuClient;
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
