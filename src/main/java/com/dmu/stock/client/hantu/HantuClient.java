package com.dmu.stock.client.hantu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class HantuClient {
    private final WebClient webClient;

    @Value("${hantu.appkey}")
    String appKey;
    @Value("${hantu.appsecret}")
    String appSecret;

    // HantuClient.java 내부
    public String getAccessToken() {
        log.info("한투 API 토큰 발급 요청 중--");

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("appkey", appKey);
        body.put("appsecret", appSecret);

        return webClient.post()
                .uri("https://openapi.koreainvestment.com:9443/oauth2/tokenP")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(HantuDto.TokenResponse.class)
                .map(res -> {
                    log.info("토큰 발급 성공! 유효시간: {}초", res.getExpires_in());
                    return res.getAccess_token();
                })
                .block(); // 테스트 단계에선 결과를 바로 확인하기 위해 block() 사용
    }
    // [2] 현재가 조회하기 (삼성전자 종목번호: 005930)
    public HantuDto.PriceResponse getStockPrice(String stockCode, String token) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("openapi.koreainvestment.com")
                        .port(9443)
                        .path("/uapi/domestic-stock/v1/quotations/inquire-price")
                        .queryParam("FID_COND_SCRN_NO", "0000")
                        .queryParam("FID_INPUT_ISCD", stockCode)
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .build())
                .header("Content-Type","application/json; charset=utf-8")
                .header("authorization", "Bearer " + token)
                .header("appkey", appKey)
                .header("appsecret", appSecret)
                .header("tr_id", "FHKST01010100") // 현재가 조회용 ID
                .header("custtype", "P")
                .retrieve()
                .bodyToMono(HantuDto.PriceResponse.class)
                .block();
    }

    // 테스트용: 서버 시작하자마자 실행됨
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            log.info("============== 테스트 시작 ==============");
            String token = getAccessToken();
            log.info("받아온 토큰 확인: {}", token);

            if (token != null) {
                // 삼성전자(005930) 현재가 조회 테스트
                HantuDto.PriceResponse price = getStockPrice("005930", token);
                log.info("삼성전자 현재가: {}원", price.getOutput().getStck_prpr());
            }
            log.info("============== 테스트 종료 ==============");
        } catch (Exception e) {
            log.error("테스트 중 에러 발생: ", e);
        }
    }
}
