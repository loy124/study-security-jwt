package com.onyou.firstproject.config.oauth;

import com.onyou.firstproject.config.oauth.provider.GoogleUserInfo;
import com.onyou.firstproject.config.oauth.provider.KakaoUserInfo;
import com.onyou.firstproject.config.oauth.provider.NaverUserInfo;
import com.onyou.firstproject.config.oauth.provider.OAuth2UserInfo;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {



    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("PrincipalOauth2UserService.loadUser");
        //type을 알아야한다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        ClientRegistration clientRegistration = userRequest.getClientRegistration();

        OAuth2UserInfo oAuth2UserInfo = null;

        //google
        if(clientRegistration.getRegistrationId().equals("google")){
            //여기서 받아온 정보를
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

        }else if(clientRegistration.getRegistrationId().equals("naver")){
            //response 안에 있다.
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));

        }else if(clientRegistration.getRegistrationId().equals("kakao")){
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }

        //TODO 회원가입 프로세스 진행하기



        //이제 회원가입을 진행한다

        //네이버 로그인 요청

        return oAuth2User;
    }
}
