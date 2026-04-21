//package com.dmu.stock.client.naver;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//@Slf4j
//public class NaverNewsClient {
//
//    private final WebClient naverWebClient;
//
//    @Value("${naver.client.id}")
//    private String clientId;
//
//    @Value("${naver.client.secret}")
//    private String clientSecret;
//
//    // Qualifier를 사용하여 네이버 전용 빈을 명시적으로 가져옵니다.
//    public NaverNewsClient(@Qualifier("naverWebClient") WebClient naverWebClient) {
//        this.naverWebClient = naverWebClient;
//    }
//
//    /**
//     * 종목명으로 뉴스 검색 후 URL 리스트 반환
//     */
//    public List<String> searchNewsName(String stockName) {
//        log.info("네이버 뉴스 검색 API 호출 - 키워드: {}", stockName);
//
//        NaverNewsResponseDto response = naverWebClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/v1/search/news.json")
//                        .queryParam("query", stockName)
//                        .queryParam("display", 10) // 5개
//                        .queryParam("sort", "sim") // 유사도 순
//                        .build())
//                .header("X-Naver-Client-Id", clientId)
//                .header("X-Naver-Client-Secret", clientSecret)
//                .retrieve()
//                .bodyToMono(NaverNewsResponseDto.class)
//                .block();
//
//        if (response == null || response.getItems() == null) {
//            return Collections.emptyList();
//        }
//
//        // URL(link)만 리스트로 뽑아서 반환
//        return response.getItems().stream()
//                .map(NaverNewsResponseDto.NaverNewsItem::getLink)
//                .filter(link -> !link.contains("video") && !link.contains("tv.naver.com"))
//                .collect(Collectors.toList());
//    }
//    /**
//     * [뉴스 검색기]
//     * @param query   검색어 (예: "삼성전자 공급망", "오늘의 증시")
//     * @param display 가져올 뉴스 개수 (1~100)
//     * @param sort    정렬 방식 ("sim": 유사도순, "date": 최신순)
//     */
//    public List<String> searchNews(String query, int display, String sort) {
//        log.info("네이버 뉴스 검색 API 호출 - 키워드: {}, 개수: {}, 정렬: {}", query, display, sort);
//
//        NaverNewsResponseDto response = naverWebClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/v1/search/news.json")
//                        .queryParam("query", query)
//                        .queryParam("display", display) // 5개
//                        .queryParam("sort", sort) // 유사도 순
//                        .build())
//                .header("X-Naver-Client-Id", clientId)
//                .header("X-Naver-Client-Secret", clientSecret)
//                .retrieve()
//                .bodyToMono(NaverNewsResponseDto.class)
//                .block();
//
//        if (response == null || response.getItems() == null) {
//            log.warn("네이버 API 응답이 비어있습니다. 검색어: {}", query);
//            return Collections.emptyList();
//        }
//
//        // URL(link)만 리스트로 뽑아서 반환
//        return response.getItems().stream()
//                .map(NaverNewsResponseDto.NaverNewsItem::getLink)
//                .filter(link -> !link.contains("video") && !link.contains("tv.naver.com"))
//                .collect(Collectors.toList());
//    }
//}
