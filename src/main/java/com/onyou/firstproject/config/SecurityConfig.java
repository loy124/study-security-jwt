package com.onyou.firstproject.config;


import com.onyou.firstproject.config.oauth.PrincipalOauth2UserService;
import com.onyou.firstproject.config.oauth.hanlder.OAuth2LoginFailureHandler;
import com.onyou.firstproject.config.oauth.hanlder.OAuth2LoginSuccessHandler;
import com.onyou.firstproject.exception.Exception401;
import com.onyou.firstproject.exception.Exception403;
import com.onyou.firstproject.member.repository.MemberRepository;
import com.onyou.firstproject.utils.JwtTokenUtil;
import com.onyou.firstproject.utils.MyFilterResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static com.onyou.firstproject.utils.JwtTokenUtil.*;

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



    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // JWT 필터 등록이 필요함
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {

            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            builder.addFilterBefore(new JwtFilter(memberRepository, secretKey), UsernamePasswordAuthenticationFilter.class);
            // 시큐리티 관련 필터
            super.configure(builder);
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {


        //CSRF 해제
        httpSecurity.csrf().disable();

        // iframe 방지
        httpSecurity.headers().frameOptions().disable();

        // 3. cors 재설정
        httpSecurity.cors().configurationSource(configurationSource());

        // 세션 방지
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        // 커스텀 필터 적용
        httpSecurity.apply(new CustomSecurityFilterManager());


        //인증 실패
        httpSecurity.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            //log.warn + message
            MyFilterResponseUtil.unAuthorized(response, new Exception401("인증되지 않았습니다"));
        });

        //권한 실패
        httpSecurity.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
//            log.warn("권한이 없는 사용자가 자원에 접근하려 합니다 : "+accessDeniedException.getMessage());
            MyFilterResponseUtil.forbidden(response, new Exception403("권한이 없습니다"));
        });


        //인증, 권한 설정
        httpSecurity.authorizeRequests(
                authorize -> authorize.antMatchers("/api/member/login").permitAll()
                        .antMatchers(HttpMethod.POST, "/api/member").permitAll()
                        .antMatchers(HttpMethod.POST, "/api/member/silent-refresh").permitAll()
                        .antMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
        );

//        httpSecurity
//
//                .authorizeRequests()
//                .antMatchers(HttpMethod.POST, "/api/**").authenticated()
//                .antMatchers("/api/member/login").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/member").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/member/silent-refresh").permitAll()
//
//                .anyRequest().permitAll();



        //Oauth2 로그인
        httpSecurity.oauth2Login()
                //동의하고 계속하기 눌렀을때
                .successHandler(oAuth2LoginSuccessHandler)
                //소셜 로그인 실패시 핸들러
                .failureHandler(oAuth2LoginFailureHandler)
                .userInfoEndpoint()
                .userService(principalOauth2UserService);

        //filter
//        httpSecurity.addFilterBefore(new JwtFilter(authenticationManager, memberRepository, secretKey), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();

    }




    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization"); // 옛날에는 디폴트 였다. 지금은 X
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
