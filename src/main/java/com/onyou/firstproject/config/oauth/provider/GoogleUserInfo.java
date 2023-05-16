package com.onyou.firstproject.config.oauth.provider;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes; //getAttributes를 받아온다.

    @Override
    public String getProviderId() {
        return (String)attributes.get("sub");
    }


    @Override
    public String getEmail() {
        return (String)attributes.get("email");
    }

    @Override
    public String getName() {
        return (String)attributes.get("name");
    }

    @Override
    public String getProvider() {
        return "google";
    }
}
