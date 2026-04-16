package com.dmu.stock.client.hantu;

import com.dmu.stock.entity.enums.StockType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class HantuClient {
    private final WebClient hantuWebClient;

    @Value("${hantu.appkey}")
    String appKey;
    @Value("${hantu.appsecret}")
    String appSecret;

    public HantuClient(@Qualifier("hantuWebClient") WebClient hantuWebClient) {
        this.hantuWebClient = hantuWebClient;
    }

    private String accessToken; // 메모리에 토큰 저장
    private LocalDateTime expiryTime; // 만료 시간 체크용

    /**
     * 한투 토큰 있는지 확인 후 없으면 생성 메서드 실행
     * @return
     */
    public String getValidToken() {
        // 토큰이 없거나, 만료시간이 1분 남았을 때 새로 갱신
        if (accessToken == null || LocalDateTime.now().isAfter(expiryTime.minusMinutes(1))) {
            refreshAccessToken();
        }
        return accessToken;
    }

    /**
     * 한투 토큰 생성
     */
    private void refreshAccessToken() {
        // 요청 바디(Body) 생성
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("appkey", appKey);
        body.put("appsecret", appSecret);

        log.info("한투 API 토큰 갱신 중...");

        HantuDto.TokenResponse res = hantuWebClient.post()
                .uri("https://openapi.koreainvestment.com:9443/oauth2/tokenP")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(HantuDto.TokenResponse.class)
                .block();

        this.accessToken = res.getAccess_token();
        log.info("토큰 발급 성공 . . . 유효시간: {}초", res.getExpires_in());
        // 유효 시간을 계산해서 저장 (보통 86400초 등)
        this.expiryTime = LocalDateTime.now().plusSeconds(res.getExpires_in());
    }

    // 현재가 조회하기 (삼성전자 종목번호: 005930)
    public HantuDto.PriceResponse getStockPrice(String stockCode, StockType type) {
        String validToken = getValidToken();
        boolean isUsa = "USA".equals(type);

        String path = isUsa
                ? "/uapi/overseas-stock/v1/quotations/price"  // 미국 주식 경로
                : "/uapi/domestic-stock/v1/quotations/inquire-price"; // 국내 주식 경로

        String trId = isUsa ? "HHDFS00000300" : "FHKST01010100";
        return hantuWebClient.get()
                .uri(uriBuilder -> {
                        uriBuilder.path(path);
                        if(isUsa){
                            uriBuilder.queryParam("AUTH", "")
                                    .queryParam("EXCD", "NAS") // 일단 나스닥 고정 (IREN은 나스닥)
                                    .queryParam("SYMB", stockCode);

                        }
                    else{
                        uriBuilder.queryParam("FID_COND_SCRN_NO", "0000")
                                .queryParam("FID_INPUT_ISCD", stockCode)
                                .queryParam("FID_COND_MRKT_DIV_CODE", "J");

                    }
                    return uriBuilder.build();
                })
                .header("Content-Type", "application/json")
                .header("authorization", "Bearer " + validToken)
                .header("appkey", appKey)
                .header("appsecret", appSecret)
                .header("tr_id", trId) // 현재가 조회용 ID
                .header("custtype", "P") //개인 회원
                .retrieve()
                .bodyToMono(HantuDto.PriceResponse.class)
                .block();
    }
}
