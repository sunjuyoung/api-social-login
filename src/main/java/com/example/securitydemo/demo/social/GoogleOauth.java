package com.example.securitydemo.demo.social;

import com.example.securitydemo.demo.social.dto.GoogleOAuth;
import com.example.securitydemo.demo.social.dto.GoogleUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth{


    @Value("${oauth2.google.url}")
    private String GOOGLE_SNS_LOGIN_URL;

    @Value("${oauth2.google.clientId}")
    private String GOOGLE_SNS_CLIENT_ID;

    @Value("${oauth2.google.redirectUri}")
    private String GOOGLE_SNS_REDIRECT_URL;

    @Value("${oauth2.google.secretId}")
    private String GOOGLE_SNS_CLIENT_SECRET;

    @Value("${oauth2.google.scope}")
    private String GOOGLE_DATA_ACCESS_SCOPE;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Override
    public String getOauthRedirectURL() {

        Map<String,Object> params=new HashMap<>();
        params.put("scope",GOOGLE_DATA_ACCESS_SCOPE);
        params.put("access_type","offline");
        params.put("include_granted_scopes","true");
        params.put("response_type","code");
        params.put("state","state_parameter_passthrough_value");
        params.put("redirect_uri",GOOGLE_SNS_REDIRECT_URL);
        params.put("client_id",GOOGLE_SNS_CLIENT_ID);
      //  params.put("client_secret",GOOGLE_SNS_CLIENT_SECRET);

        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString=params.entrySet().stream()
                .map(x->x.getKey()+"="+x.getValue())
                .collect(Collectors.joining("&"));
        String redirectURL=GOOGLE_SNS_LOGIN_URL+"?"+parameterString;
        System.out.println("redirectURL = " + redirectURL);

        return redirectURL;
    }

    public ResponseEntity<String> requestAccessToken(String code) {
        String GOOGLE_TOKEN_REQUEST_URL="https://oauth2.googleapis.com/token";
        RestTemplate restTemplate=new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        params.put("redirect_uri", GOOGLE_SNS_REDIRECT_URL);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> responseEntity=restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL,
                params,String.class);

        if(responseEntity.getStatusCode()== HttpStatus.OK){
            return responseEntity;
        }
        return null;
    }

    //reponseEntity에 담긴 JSON 역직렬화 (객체에 넣는다)
    public GoogleOAuth  getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        System.out.println("response.getBody() = " + response.getBody());
        GoogleOAuth googleOAuthToken= objectMapper.readValue(response.getBody(),GoogleOAuth.class);
        return googleOAuthToken;
    }

    public ResponseEntity<String> requestUserInfo(GoogleOAuth googleOAuth) {
        String GOOGLE_USERINFO_REQUEST_URL="https://www.googleapis.com/oauth2/v1/userinfo";

        //header에 accessToken을 담는다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+googleOAuth.getAccess_token());

        //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> response= restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET,request,String.class);
        System.out.println("response.getBody() = " + response.getBody());
        return response;
    }

    public GoogleUser getUserInfo(ResponseEntity<String> userInfo) throws JsonProcessingException {
        GoogleUser googleUser=objectMapper.readValue(userInfo.getBody(),GoogleUser.class);
        return googleUser;
    }
}
