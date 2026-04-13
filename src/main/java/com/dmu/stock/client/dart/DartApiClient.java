package com.dmu.stock.client.dart;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class DartApiClient {
    private final WebClient dartWebClient;

    @Value("${dart.api.key}")
    private String dartApiKey;

    public DartApiClient(@Qualifier("dartWebClient") WebClient dartWebClient) {
        this.dartWebClient = dartWebClient;
    }


    public Mono<String> getDisclosureList(String corpCode) {
        return dartWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/list.json")
                        .queryParam("crtfc_key", dartApiKey)
                        .queryParam("corp_code", corpCode)
                        .queryParam("bgn_de", "20240101") // 시작 날짜
                        .queryParam("pblntf_ty", "A")    // 정기공시
                        .build())
                .retrieve()
                .bodyToMono(String.class); // 일단 String으로 받고 나중에 DTO로 변환하세요!
    }

}
