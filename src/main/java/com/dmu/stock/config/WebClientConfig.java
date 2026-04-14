package com.dmu.stock.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // 커넥션 타임아웃 및 응답 시간 제한 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 연결 시도 시간 (10초)
                .responseTimeout(Duration.ofSeconds(10))            // 응답 대기 시간 (10초)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

        // 빌더에 위 설정 주입 및 메모리 확장 적용
        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    @Bean
    public WebClient dartWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://opendart.fss.or.kr")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient fastapiWebClient(WebClient.Builder builder) {

        //ai 분석 응답 대기 60초
        HttpClient longTimeOutClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(60))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS)));
        return builder
                .baseUrl("http://localhost:8000") // FastAPI 서버 주소
                .clientConnector(new ReactorClientHttpConnector(longTimeOutClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // 한국투자증권 API용 클라이언트
    @Bean
    public WebClient hantuWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://openapi.koreainvestment.com:9443")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .build();
    }

    // 네이버 뉴스 검색 API용 클라이언트
    @Bean
    public WebClient naverWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://openapi.naver.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
