package com.example.securitydemo.demo.social;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOauth implements SocialOauth{


    @Override
    public String getOauthRedirectURL() {
        return null;
    }
}
