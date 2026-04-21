package com.dmu.stock.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JWTProvider {
    private final SecretKey secretKey;

    public JWTProvider(@Value("${spring.jwt.secretkey}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userEmail", String.class);
    }

    public Boolean isTokenExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String getCategory(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseClaimsJws(token).getPayload().get("category", String.class);
    }

    public String generateToken(String category, String userEmail, /*String role,*/Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("userEmail", userEmail)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch(SecurityException | MalformedJwtException e){
            log.debug("잘못된 JWT 서명입니다.",e);
        } catch(IllegalArgumentException e){
            log.debug("JWT 토큰이 잘못되었습니다.",e);
        } catch(ExpiredJwtException e){
            log.debug("만료된 JWT 토큰입니다.",e);
        } catch(UnsupportedJwtException e){
            log.debug("지원되지 않는 JWT 토큰입니다.",e);
        }

        return false;
    }

    /**
     * access token의 남은 유효시간을 반환하는 메소드
     * 로그아웃 시 access token의 남은 유효시간을 확인하기 위해서는 토큰의 만료 시간(expiration)을 가져와서 현재 시간과의 차이를 계산하는 메소드 필요
     */
    public long getRemainingMillis(String token) {
        Date expiration = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                .getPayload().getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }
}
