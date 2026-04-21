package com.dmu.stock.client.hantu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class HantuDto {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TokenResponse {
        private String access_token;
        private String token_type;
        private Long expires_in;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceResponse {

        @JsonProperty("output")
        private Output output;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Output {

            @JsonProperty("stck_prpr") // 현재가
            private String stck_prpr;

            @JsonProperty("prdy_vrss") // 전일 대비
            private String prdy_vrss;

            @JsonProperty("hts_avls") // 시가총액
            private String hts_avls;

            @JsonProperty("prdy_ctrt") // 등락률
            private String prdy_ctrt;

            // --- 미국 주식 필드 추가 ---
            @JsonProperty("last") private String last; // 현재가 (미국)
            @JsonProperty("rate") private String rate; // 등락률 (미국)

            public double getNumericPrice() {
                String price = (last != null) ? last : stck_prpr;
                return parseDoubleSafe(price);
            }

            public double getNumericChange() {
                return parseDoubleSafe(prdy_vrss);
            }

            public double getNumericRate() {
                String r = (rate != null) ? rate : prdy_ctrt;
                return parseDoubleSafe(r);
            }

            public long getNumericMarketCap() {
                try {
                    return Long.parseLong(hts_avls);
                } catch (NumberFormatException e) {
                    return 0L;
                }
            }
            private double parseDoubleSafe(String value) {
                try {
                    return (value == null || value.isEmpty()) ? 0.0 : Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }
    }

    @Data
    public static class BalanceResponse {
        private String rt_cd;   // 성공 실패 여부 (0: 성공)
        private String msg_cd;  // 응답 코드
        private String msg1;    // 응답 메시지

        @JsonProperty("output1")
        private List<StockBalanceResponse> output1; // 종목 리스트
    }
    @Data
    public static class StockBalanceResponse {
        @JsonProperty("pdno")
        private String pdno;            // 종목번호 (상품번호)

        @JsonProperty("prdt_name")
        private String prdtName;        // 종목명

        @JsonProperty("hldg_qty")
        private String hldgQty;         // 보유수량

        @JsonProperty("pchs_avg_pric")
        private String pchsAvgPric;     // 매입평균가격 (내 평단가)

        @JsonProperty("prpr")
        private String prpr;            // 현재가

        @JsonProperty("evlu_pfls_amt")
        private String evluPflsAmt;     // 평가손익금액 (수익금)

        @JsonProperty("evlu_pfls_rt")
        private String evluPflsRt;      // 평가손익률 (수익률 %)

        @JsonProperty("evlu_amt")
        private String evluAmt;         // 평가금액 (현재 가치 합계)
    }
}