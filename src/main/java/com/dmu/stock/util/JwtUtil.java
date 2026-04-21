package com.dmu.stock.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component  // Spring Bean으로 등록
public class JwtUtil {
    private final SecretKey secretKey;

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    // 생성자에서 문자열 키를 HMAC 보안 키 객체로 변환
    public JwtUtil(@Value("${jwt.secret.key}") String secretKeyString) {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString); // 인코딩 적용 코드
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access Token 생성
    public String generateAccessToken(String userEmail) {
        return Jwts.builder()
                .subject(userEmail)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    // Refresh Token 생성
    public  String generateRefreshToken(String userEmail) {
        return Jwts.builder()
                .subject(userEmail)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    // Access Token 검증
    public Claims validateAccessToken(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return null;
        }
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // 헤더가 비어있거나 "Bearer "로 시작하지 않으면 에러 또는 null 반환
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("유효한 Authorization 헤더가 없습니다.");
            return null;
            // 또는 throw new CustomException(ErrorType.INVALID_TOKEN);
        }

        return authHeader.substring(7); // "Bearer " 제거 후 순수 토큰만 반환
    }
}
