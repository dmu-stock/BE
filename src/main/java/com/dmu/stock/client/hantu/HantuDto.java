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

            public double getNumericPrice() {
                return parseDoubleSafe(stck_prpr);
            }

            public double getNumericChange() {
                return parseDoubleSafe(prdy_vrss);
            }

            public double getNumericRate() {
                return parseDoubleSafe(prdy_ctrt);
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
}