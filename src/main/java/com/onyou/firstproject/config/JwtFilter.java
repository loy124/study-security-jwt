package com.onyou.firstproject.config;

import com.onyou.firstproject.config.auth.PrincipalDetails;
import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.repository.MemberRepository;
import com.onyou.firstproject.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
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

    private final AuthenticationManager authenticationManager;

    private final MemberRepository memberRepository;

    private final String secretKey;

    //Username Token에서 꺼내기
    String email =  "";



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JwtFilter.doFilterInternal");
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization", authorization );

        //Token 꺼내기
        String accessToken = getAccessToken(authorization);

        boolean isExpiredOrIssue = JwtTokenUtil.isExpired(accessToken, secretKey);

        String refreshToken = getRefreshTokenFromCookie(request);

        // 1. 액세스 토큰 검증
        // 2. 액세스토큰 만료시 refresh token 검증
        // 3. refresh 만료가 가까워질때 refresh token 재발급
        //TODO 4. Oauth 진행하기
        if(isExpiredOrIssue) {
            log.error("access 토큰이 만료되었습니다.");

            // 액세스가 만료되면서 refresh도 만료되었기 때문에
            if(refreshToken == null || JwtTokenUtil.isExpired(refreshToken, secretKey)){
                log.error("refresh 토큰이 없거나 만료되었습니다.");
                filterChain.doFilter(request, response);
                return;
            }


            String parsingEmailFromRefreshToken = getEmail(refreshToken);

            if(parsingEmailFromRefreshToken != null){

                //refresh 토큰이 유효한 상태기때문에 accessToken을 재발급한다.
                accessToken = JwtTokenUtil.createToken(parsingEmailFromRefreshToken, secretKey, accessExpireTimeMs);

            }

        }


        //만료 안된 accessToken이 발급되는 상태

        String email = getEmail(accessToken);
        log.info("email", email);

        boolean isOneWeeksLeft = JwtTokenUtil.isWithinOneWeek(refreshToken, secretKey);
        //만료가 되지 않는상태에서 refresh 검증후 1주일 이내인 상태라면 재발급하는 로직
        if(isOneWeeksLeft){
            logger.info("만료가 되지 않은 상태기때문에 refresh를 재발급해주는 로직");
            String parsedEmail = getEmail(refreshToken);
            setRefreshToken(parsedEmail,response);
        }

        //TODO OAuth인 경우

        //권한 부여
        UsernamePasswordAuthenticationToken authenticationToken = getUsernamePasswordAuthenticationToken(email);

        // Detail을 넣어줍니다
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);

    }

    private static String getAccessToken(String authorization) {
            try {
                String[] splits = authorization.split(" ");

                if(authorization == null || !authorization.startsWith("Bearer ")){
                    return null;
                }
                if(authorization == null || splits.length == 0){
                    return null;
                }

                return splits[1];
            }catch (Exception e){
                return null;
            }
    }

    private String getEmail(String refreshToken) {
        return JwtTokenUtil.getEmail(refreshToken, secretKey);
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String email) {
        Member member = memberRepository.findJoinByEmail(email);
        PrincipalDetails principalDetails = new PrincipalDetails(member);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principalDetails, null,principalDetails.getAuthorities());
        return authenticationToken;
    }

    public void setRefreshToken(String email, HttpServletResponse response) {
        String refreshToken = JwtTokenUtil.createToken(email, secretKey, refreshExpireTimeMs);
        Cookie cookie = new Cookie("refresh-token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
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
