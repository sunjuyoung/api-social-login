package com.example.securitydemo.demo.social;


import com.example.securitydemo.demo.social.dto.GoogleOAuth;
import com.example.securitydemo.demo.social.dto.NaverOAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NaverOauth implements SocialOauth{

    private static final String GRANT_TYPE = "authorization_code";
    @Value("${oauth2.naver.url}")
    private String NAVER_LOGIN_URL;

    @Value("${oauth2.naver.clientId}")
    private String NAVER_CLIENT_ID;

    @Value("${oauth2.naver.redirectUri}")
    private String NAVER_REDIRECT_URL;

    @Value("${oauth2.naver.secretId}")
    private String NAVER_CLIENT_SECRET;

    @Value("${oauth2.naver.tokenUri}")
    private String NAVER_TOKEN_URL;


    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    @Override
    public String getOauthRedirectURL() {
        Map<String,Object> params=new HashMap<>();
        params.put("response_type","code");
        params.put("client_id",NAVER_CLIENT_ID);
        params.put("redirect_uri",NAVER_REDIRECT_URL);
        params.put("state","hLiDdL2uhPtsftcU");

        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString=params.entrySet().stream()
                .map(x->x.getKey()+"="+x.getValue())
                .collect(Collectors.joining("&"));
        String redirectURL=NAVER_LOGIN_URL+"?"+parameterString;
        System.out.println("redirectURL = " + redirectURL);

        return redirectURL;
    }

    public String requestAccessToken(String code) {
        String NAVER_TOKEN_REQUEST_URL=NAVER_TOKEN_URL;
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", NAVER_CLIENT_ID);
        body.add("client_secret", NAVER_CLIENT_SECRET);
        body.add("code", code);
        body.add("state", "hLiDdL2uhPtsftcU");

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        NaverOAuthToken response = restTemplate.postForObject(NAVER_TOKEN_REQUEST_URL, request, NaverOAuthToken.class);

        assert response != null;
        return response.getAccessToken();
    }

    public ResponseEntity<String> requestUserInfo(String  accessToken)  {
        String NAVER_USERINFO_REQUEST_URL="https://openapi.naver.com/v1/nid/me";

        //header에 accessToken을 담는다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+accessToken);

        //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> response= restTemplate.exchange(NAVER_USERINFO_REQUEST_URL, HttpMethod.GET,request,String.class);
        System.out.println("response.getBody() = " + response.getBody());
        return response;
    }

}
