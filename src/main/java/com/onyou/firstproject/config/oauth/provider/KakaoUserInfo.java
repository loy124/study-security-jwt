package com.onyou.firstproject.config.oauth.provider;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes; //getAttributes를 받아온다.

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }


    @Override
    public String getEmail() {
        Map kakaoAccount = (Map) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        return email;
    }

    @Override
    //profile의 nickname에 있다.
    public String getName() {

        Map properties = (Map) attributes.get("properties");
        String nickname = (String) properties.get("nickname");

        return nickname;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

}
