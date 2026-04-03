package com.dmu.stock.client.hantu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
        }
    }
}