package com.dmu.stock.jwt;

import io.jsonwebtoken.*;
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
                .claim("category", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    // Refresh Token 생성
    public  String generateRefreshToken(String userEmail) {
        return Jwts.builder()
                .subject(userEmail)
                .claim("category", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    // 유저 이메일 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject(); // subject로 저장했으니 getSubject()로 가져옴
    }

    // Access Token 검증
    public boolean validateToken(String accessToken, String expectedCategory) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();

            String category = claims.get("category", String.class);
            if (category == null || !category.equals(expectedCategory)) {
                log.error("토큰 카테고리 불일치. 기대값: {}, 실제값: {}", expectedCategory, category);
                return false;
            }

            return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다: {}", e.getMessage());
        }
        return false;
    }

    // Access Token 전용
    public boolean validateAccessToken(String token) {
        return validateToken(token, "access");
    }

    // Refresh Token 전용 (재발급 컨트롤러에서 사용)
    public boolean validateRefreshToken(String token) {
        return validateToken(token, "refresh");
    }

    //헤더에서 토큰 추출
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

    // 로그아웃용 잔여 시간 계산 (추후 redis 및 블랙리스트 기능 추가할 때 사용 예정)
    public long getRemainingMillis(String token) {
        Date expiration = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                .getPayload().getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }
}
