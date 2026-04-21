package com.dmu.stock.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTProvider jwtProvider;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = authorization.split(" ")[1];

        // 3. 토큰 유효성 및 만료 체크
        try {
            // validateToken을 사용해서 서명 변조 및 만료 여부를 한 번에 체크할 수도 있습니다.
            if (!jwtProvider.validateToken(accessToken)) {
                JwtExceptionResponseUtil.unAuthentication(response, JWTErrorType.INVALID_TOKEN_ERROR);
                return;
            }
        } catch (Exception e) {
            JwtExceptionResponseUtil.unAuthentication(response, JWTErrorType.INVALID_TOKEN_ERROR);
            return;
        }

        // 4. Access 토큰인지 확인 (Refresh 토큰으로 접근 방지)
        String category = jwtProvider.getCategory(accessToken);
        if (!"access".equals(category)) {
            JwtExceptionResponseUtil.unAuthentication(response, JWTErrorType.INVALID_TOKEN_ERROR);
            return;
        }

        // 5. 유저 정보 추출 및 시큐리티 컨텍스트 등록
        String userEmail = jwtProvider.getUsernameFromToken(accessToken);

        // DB 조회를 통해 최신 유저 정보를 가져옴
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailService.loadUserByUsername(userEmail);

        // 시큐리티 전용 인증 객체 생성
        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 핵심: 시큐리티 금고에 저장 (이걸 해야 @AuthenticationPrincipal 작동)
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/book/search");

    }
}
