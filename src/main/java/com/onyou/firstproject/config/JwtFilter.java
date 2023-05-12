package com.onyou.firstproject.config;

import com.onyou.firstproject.config.auth.PrincipalDetails;
import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.repository.MemberRepository;
import com.onyou.firstproject.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.onyou.firstproject.utils.JwtTokenUtil.accessExpireTimeMs;
import static com.onyou.firstproject.utils.JwtTokenUtil.refreshExpireTimeMs;

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
        boolean expired = false;
        try {
            expired = JwtTokenUtil.isExpired(token, secretKey);
        }
        catch (Exception e){
            logger.error(e);
        }


        // 1. 액세스 토큰 검증
        // 2. 액세스토큰 만료시 refresh token 검증
        // TODO 3. refresh 만료가 가까워질때 refresh token 재발급
        //TODO 4. Oauth 진행하기
        // Token Expired되었는지 여부
        if(expired) {
            log.error("access 토큰이 만료되었습니다.");


            String refreshToken = getRefreshTokenFromCookie(request);


            // 액세스가 만료되면서 refresh도 만료되었기 때문에
            if(refreshToken == null || JwtTokenUtil.isExpired(refreshToken, secretKey)){
                log.error("refresh 토큰이 만료되었습니다.");
                filterChain.doFilter(request, response);
                return;
            }
//            eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImRoc2RiMTU0MUBuYXZlci5jb20iLCJpYXQiOjE2ODM4NjgwMjgsImV4cCI6MTY4NTA3NzYyOH0.jXUmIR_TT2nTVQggtf8fumhKOWWkX3wPukEE739Kcok
            String parsingEmailFromRefreshToken = JwtTokenUtil.getEmail(refreshToken, secretKey);

            if(parsingEmailFromRefreshToken != null){

                //refresh 토큰이 유효한 상태기때문에 accessToken을 재발급한다.
                token = JwtTokenUtil.createToken(parsingEmailFromRefreshToken, secretKey, accessExpireTimeMs);

            }

        }

        //만료 안된 accessToken이 발급되는 상태

        String email = JwtTokenUtil.getEmail(token, secretKey);
        log.info("email", email);


        //권한 부여
        UsernamePasswordAuthenticationToken authenticationToken = getUsernamePasswordAuthenticationToken(email);

        // Detail을 넣어줍니다
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);

    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String email) {
        Member member = memberRepository.findJoinByEmail(email);
        PrincipalDetails principalDetails = new PrincipalDetails(member);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principalDetails, null,principalDetails.getAuthorities());
        return authenticationToken;
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh-token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}