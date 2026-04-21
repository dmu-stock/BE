package com.dmu.stock.client.hantu;

import com.dmu.stock.entity.enums.StockType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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

    private volatile String accessToken; // 메모리에 토큰 저장
    private volatile LocalDateTime expiryTime; // 만료 시간 체크용


    /**
     * 서버 시작 시 자동으로 토큰 발급
     * ApplicationReadyEvent는 모든 빈이 로드되고 앱이 준비되었을 때 실행됩니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            log.info(">>> 서버 시작: 한투 API 초기 토큰 발급 시도");
            refreshAccessToken();
        } catch (Exception e) {
            log.error(">>> 초기 토큰 발급 실패! (네트워크 혹은 키 설정 확인 필요): {}", e.getMessage());
        }
    }
    /**
     * 한투 토큰 있는지 확인 후 없으면 생성 메서드 실행
     * @return
     */
    public String getValidToken() {
        // 토큰이 없거나, 만료시간이 1분 남았을 때 새로 갱신
        if (accessToken == null ||expiryTime == null || LocalDateTime.now().isAfter(expiryTime.minusMinutes(1))) {
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
                .uri("https://openapivts.koreainvestment.com:29443/oauth2/tokenP")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(HantuDto.TokenResponse.class)
                .block();
        if (res != null && res.getAccess_token() != null) {
            this.accessToken = res.getAccess_token();
            this.expiryTime = LocalDateTime.now().plusSeconds(res.getExpires_in());
            log.info("토큰 발급 성공 . . . 유효시간: {}초", res.getExpires_in());
        }else{
            log.error("토큰 발급 실패: 응답 데이터가 없습니다.");
        }
    }

    // 현재가 조회하기 (삼성전자 종목번호: 005930)
    public HantuDto.PriceResponse getStockPrice(String stockCode) {
        StockType type = StockType.detectType(stockCode);
        boolean isUsa = (type == StockType.USA);

        String path = isUsa
                ? "/uapi/overseas-price/v1/quotations/price"  // 미국 주식 경로
                : "/uapi/domestic-stock/v1/quotations/inquire-price"; // 국내 주식 경로

        String trId = isUsa ? "HHDFS00000300" : "FHKST01010100";
        return hantuWebClient.get()
                .uri(uriBuilder -> {
                        uriBuilder.path(path);
                        if(isUsa){
                            uriBuilder.queryParam("AUTH", "")
                                    .queryParam("EXCD", "NAS")
                                    .queryParam("SYMB", stockCode);

                        }
                    else{
                        uriBuilder.queryParam("FID_COND_SCRN_NO", "0000")
                                .queryParam("FID_INPUT_ISCD", stockCode)
                                .queryParam("FID_COND_MRKT_DIV_CODE", "J");

                    }
                    return uriBuilder.build();
                })
                .header("authorization", "Bearer " + accessToken)
                .header("appkey", appKey)
                .header("appsecret", appSecret)
                .header("tr_id", trId) // 현재가 조회용 ID
                .header("custtype", "P") //개인 회원
                .retrieve()
                .bodyToMono(HantuDto.PriceResponse.class)
                .block();
    }
    public List<HantuDto.StockBalanceResponse> getMyBalance(){
        return hantuWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/trading/inquire-balance")
                        .queryParam("CANO", "50183571")
                        .queryParam("ACNT_PRDT_CD", "01")
                        .queryParam("AFHR_FLPR_YN", "N")
                        .queryParam("OFL_YN", "")
                        .queryParam("INQR_DVSN", "01") // 01: 종목별 합산
                        .queryParam("UNPR_DVSN", "01")
                        .queryParam("FUND_STTL_ICLD_YN", "N")
                        .queryParam("FSRB_RESA_ET_ICLD_YN", "N")
                        .queryParam("FNCG_AMT_AUTO_RDPT_YN", "N") // 융자상환여부
                        .queryParam("PRCS_DVSN", "00")
                        .queryParam("CTX_AREA_FK100", "")
                        .queryParam("CTX_AREA_NK100", "")// 처리구분 (00: 전일매매포함)
                        .build())
                .header("authorization", "Bearer " + accessToken)
                .header("appkey", appKey)
                .header("appsecret", appSecret)
                .header("tr_id", "VTTC8434R") // 모의투자용
                .header("custtype", "P")
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class).map(body -> {
                            log.error("잔고조회 실패 상세 사유: {}", body);
                            return new RuntimeException("Hantu API Error");
                        })
                )
                .bodyToMono(HantuDto.BalanceResponse.class)
                .map(res -> res.getOutput1())
                .block();
    }
}
