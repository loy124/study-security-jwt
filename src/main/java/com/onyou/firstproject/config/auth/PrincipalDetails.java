package com.onyou.firstproject.config.auth;

import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.entity.MemberRole;
import com.onyou.firstproject.member.entity.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor

public class PrincipalDetails implements UserDetails, OAuth2User {

    private final Member member;
    private Map<String, Object> attributes;


    @Override
    public Map<String, Object> getAttributes() {

        return attributes;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        List<MemberRole> memberRoles = member.getMemberRoles();
        for (MemberRole memberRole : memberRoles) {
            RoleName roleName = memberRole.getRole().getRoleName();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
        }

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

    @Override
    public String getName() {
        return null;
    }
}
