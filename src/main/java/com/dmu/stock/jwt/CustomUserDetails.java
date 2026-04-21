package com.dmu.stock.jwt;

import com.dmu.stock.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //유저 권한 반환
        return List.of();
    }

    @Override
    public String getPassword() {
        //유저 비밀번호 물어보는 통로
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        //유저 이메일 물어보는 통로
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        //계정 만료 상태
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
