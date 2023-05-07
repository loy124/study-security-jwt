package com.onyou.firstproject.config.auth;

import com.onyou.firstproject.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails {

    private final Member member;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

//        authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRole().toString()));

        return authorities;
    }

    public Member getMember(){
        return member;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /*
    * 메인이 이메일이기 때문에 email을 리턴한다.
    * */
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    /**
     * 사용자 계정이 만료되었는지 나타낸다
     *
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    /**
     * 사용자 계정이 잠겨있는지 나타낸다
     *
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    /**
     * 사용자 계정의 증명이 만료되었는지 나타낸다
     *
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    /**
     * 사용자 계정이 사용가능한지 나타낸다
     *
     */

    @Override
    public boolean isEnabled() {
        return true;
    }
}
