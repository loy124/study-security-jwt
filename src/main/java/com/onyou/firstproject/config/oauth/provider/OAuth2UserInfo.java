package com.onyou.firstproject.config.oauth.provider;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
    String getProvider();
}
