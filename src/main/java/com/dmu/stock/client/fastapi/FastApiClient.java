package com.dmu.stock.client.fastapi;

import com.dmu.stock.dto.RagMyStockRequestDto;
import com.dmu.stock.dto.StockAnalysisRequestDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class FastApiClient {

    private final WebClient fastapiWebClient;

    public FastApiClient(@Qualifier("fastapiWebClient") WebClient fastapiWebClient) {
        this.fastapiWebClient = fastapiWebClient;
    }


    public Mono<String> analyzeMyStock(RagMyStockRequestDto dto) {
        return fastapiWebClient.post()
                .uri("/api/v1/rag/NewsAnalysis")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(String.class);
    }
    public Mono<String> analyzeStock(StockAnalysisRequestDto dto) {
        return fastapiWebClient.post()
                .uri("/api/v1/StockAnalysis") // 요청 주소
                .bodyValue(dto)               // 데이터 담기
                .retrieve()
                .bodyToMono(String.class);
    }

}
