package com.example.securitydemo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        if(registrationId.equals("google")){
            System.out.println( attributes.get("sub"));
            System.out.println( attributes.get("email"));
            System.out.println( attributes.get("name"));
            System.out.println( attributes.get("picture"));

        }else if(registrationId.equals("kakao")){
            System.out.println(attributes.get("kakao_account"));
            System.out.println(attributes.get("id"));


        }else if (registrationId.equals("github")){
            System.out.println(attributes.get("login"));
            System.out.println(attributes.get("id"));
            System.out.println(attributes.get("avatar_url"));
        }

        log.info("userRequest.....");



        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")), attributes, "id");

     //   return user;
    }
}
