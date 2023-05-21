package com.onyou.firstproject.config.oauth.hanlder;

import com.onyou.firstproject.config.auth.PrincipalDetails;
import com.onyou.firstproject.exception.Exception403;
import com.onyou.firstproject.exception.Exception404;
import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.repository.MemberRepository;
import com.onyou.firstproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    /**
     * TODO 회원가입하기 (service 호출하기 진행하면될듯?) -> 로그인 진행하기 ㄴ
     * TODO 이미 회원 가입된 경우라면 로그인 진행
     */

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Value("${user.url}")
    private String url;



    @Override

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        Member oauthMember = principal.getMember();

        Optional<Member> findMember = memberRepository.findByEmail(oauthMember.getEmail());
//
//
//        //아이디가 존재하는 경우
//        // 해당 멤버를 리턴처리 하기
        if(findMember.isPresent()){
                //로그인 처리
            setTokenAndRedirect(response, findMember.get());
            return;
        };

        //회원가입 + 로그인
        try {
            Long signUpId = memberService.signUp(oauthMember);
            Member member = memberRepository.findById(signUpId).orElseThrow(() -> new Exception404("가입한 회원이 없습니다"));
            setTokenAndRedirect(response, member);
        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new Exception403(e.toString());
        }



    }

    private void setTokenAndRedirect(HttpServletResponse response, Member member) throws IOException {
        String accessToken = memberService.createToken(member.getEmail());
        memberService.setRefreshToken(member.getEmail(), response);
        //프론트로 리다이렉트 시키기

        response.sendRedirect(url);

    }
}
