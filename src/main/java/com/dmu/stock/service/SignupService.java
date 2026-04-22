package com.dmu.stock.service;

import com.dmu.stock.client.security.SecurityConfig;
import com.dmu.stock.dto.SignupReqDto;
import com.dmu.stock.entity.Member;
import com.dmu.stock.exception.CustomException;
import com.dmu.stock.exception.ErrorType;
import com.dmu.stock.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignupService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupReqDto request){
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(m-> {
                    throw new CustomException(ErrorType.ALREADY_EXIST_MEMBER);
                });
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .phone(request.getPhone())
                .createdAt(LocalDateTime.now())
                .build();
         memberRepository.save(member);

    }
}
