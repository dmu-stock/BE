package com.dmu.stock.service;

import com.dmu.stock.dto.LoginResDto;
import com.dmu.stock.entity.Member;
import com.dmu.stock.exception.CustomException;
import com.dmu.stock.repository.MemberRepository;
import com.dmu.stock.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.dmu.stock.exception.ErrorType.INVALID_PASSWORD;
import static com.dmu.stock.exception.ErrorType.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public LoginResDto login(String email, String password){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        //dirty checking으로 저장
        member.updateRefreshToken(refreshToken);

        return new LoginResDto(accessToken, refreshToken);
    }

}
