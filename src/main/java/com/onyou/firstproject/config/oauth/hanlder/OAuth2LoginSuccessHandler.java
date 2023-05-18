package com.onyou.firstproject.config.oauth.hanlder;

import com.onyou.firstproject.config.auth.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    /**
     * TODO 회원가입하기 (service 호출하기 진행하면될듯?) -> 로그인 진행하기 ㄴ
     * TODO 이미 회원 가입된 경우라면 로그인 진행
     */


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        System.out.println("오나ㅑ");
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("오나ㅑ");
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        //TODO 마 토큰 발급해봐라 마!


    }
}
