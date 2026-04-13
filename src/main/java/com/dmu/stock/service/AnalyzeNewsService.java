package com.dmu.stock.service;

import com.dmu.stock.client.fastapi.FastApiClient;
import com.dmu.stock.client.hantu.HantuClient;
import com.dmu.stock.client.naver.NaverNewsClient;
import com.dmu.stock.client.naver.NewsAnalysisRequestDto;
import com.dmu.stock.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyzeNewsService {

    private final HantuClient hantuClient;
    private final NaverNewsClient naverNewsClient;
    private final FastApiClient fastApiClient;

    public List<String> getNewsByName(List<String> stockCodes) {
        List<String> allUrls = new ArrayList<>();

        for (String code : stockCodes) {
            List<String> urlsPerStock = naverNewsClient.searchNewsName(code);

            allUrls.addAll(urlsPerStock);
        }
        //distinct : 중복 걸러줌
        return allUrls.stream().distinct().toList();
    }

    /**
     *
     * @param query
     * @param amount
     * @return
     */
    public Mono<String> getNews(String query, int amount) {
        List<String> urls = naverNewsClient.searchNews(query, amount, "sim");
        NewsAnalysisRequestDto requestDto = NewsAnalysisRequestDto.builder()
                .query(query)
                .urls(urls)
                .build();

        return fastApiClient.analyzeNews(requestDto);
    }
}
