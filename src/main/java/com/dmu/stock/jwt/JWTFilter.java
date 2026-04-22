package com.dmu.stock.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.extractToken(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 통합 검증 (유효성 + 만료 + 카테고리 확인을 한 번에)
        if (!jwtUtil.validateAccessToken(accessToken)) {
            JwtExceptionResponseUtil.unAuthentication(response, JWTErrorType.INVALID_TOKEN_ERROR);
            return;
        }

        try{
            // 5. 유저 정보 추출 및 시큐리티 컨텍스트 등록
            String userEmail = jwtUtil.getUsernameFromToken(accessToken);

            // DB 조회를 통해 최신 유저 정보를 가져옴
            CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailService.loadUserByUsername(userEmail);

            // 시큐리티 전용 인증 객체 생성
            Authentication authenticationToken =
                    new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            // 핵심: 시큐리티 금고에 저장 (이걸 해야 @AuthenticationPrincipal 작동)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }catch (UsernameNotFoundException e){
            log.error("User not found: {}", e.getMessage());
            JwtExceptionResponseUtil.unAuthentication(response, JWTErrorType.USER_NOT_FOUND);
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/login")
                ||path.startsWith("/api/signup")
                || path.startsWith("/index.html");

    }
}
