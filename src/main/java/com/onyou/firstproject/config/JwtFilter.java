package com.onyou.firstproject.config;

import com.onyou.firstproject.config.auth.PrincipalDetails;
import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.repository.MemberRepository;
import com.onyou.firstproject.utils.JwtTokenUtil;
import com.onyou.firstproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {


    private final MemberRepository memberRepository;

    private final String secretKey;

    //Username Token에서 꺼내기
    String email =  "";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authentication", authorization );
        if(authorization == null || !authorization.startsWith("Bearer ")){
            log.error("authentication에 이슈가 있습니다");
            filterChain.doFilter(request, response);
            return;
        }

        //Token 꺼내기
        String token = authorization.split(" ")[1];

        // Token Expired되었는지 여부
        if(JwtTokenUtil.isExpired(token, secretKey)) {
            log.error("토큰이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }
        String email = JwtTokenUtil.getEmail(token, secretKey);
        log.info("email", email);

        //권한 부여
        Member member = memberRepository.findJoinByEmail(email);
        PrincipalDetails principalDetails = new PrincipalDetails(member);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principalDetails, null,principalDetails.getAuthorities());

        // Detail을 넣어줍니다
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);

    }
}
