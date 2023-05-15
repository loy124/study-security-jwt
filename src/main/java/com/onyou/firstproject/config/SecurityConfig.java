package com.onyou.firstproject.config;


import com.onyou.firstproject.config.auth.PrincipalDetails;
import com.onyou.firstproject.config.oauth.PrincipalOauth2UserService;
import com.onyou.firstproject.config.oauth.hanlder.OAuth2LoginFailureHandler;
import com.onyou.firstproject.config.oauth.hanlder.OAuth2LoginSuccessHandler;
import com.onyou.firstproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Value("${jwt.token.secret}")
    private String secretKey;
    @Autowired
    private CorsFilter corsFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(corsFilter)
                .addFilterBefore(new JwtFilter(memberRepository, secretKey), UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests()

                .antMatchers("/api/member/login").permitAll()
                .antMatchers(HttpMethod.POST,"/api/member").permitAll()
                .antMatchers(HttpMethod.POST,"/api/member/silent-refresh").permitAll()
                .antMatchers(HttpMethod.POST, "/api/**").authenticated()
                .and()
                .oauth2Login()
                //동의하고 계속하기 눌렀을때
                //여기서 jwt 로그인하고 발급하기
                .successHandler(oAuth2LoginSuccessHandler)
                //소셜 로그인 실패시 핸들러
                .failureHandler(oAuth2LoginFailureHandler)
                .userInfoEndpoint().userService()
                .build();

    }
}
