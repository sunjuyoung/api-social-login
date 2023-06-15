package com.example.securitydemo.demo;

import com.example.securitydemo.demo.account.Account;
import com.example.securitydemo.demo.account.AccountRepository;
import com.example.securitydemo.demo.social.GoogleOauth;
import com.example.securitydemo.demo.social.KakaoOauth;
import com.example.securitydemo.demo.social.NaverOauth;
import com.example.securitydemo.demo.social.dto.GoogleOAuth;
import com.example.securitydemo.demo.social.dto.GoogleUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class DemoService {

    private final AccountRepository accountRepository;

    private final KakaoOauth kakaoOauth;
    private final GoogleOauth googleOauth;

    private final NaverOauth naverOauth;
    private final HttpServletResponse response;

    private final PasswordEncoder passwordEncoder;

    public void request(String socialLoginType) throws IOException {

        switch (socialLoginType) {
            case "kakao":
                response.sendRedirect(kakaoOauth.getOauthRedirectURL());
                break;
            case "google":
                response.sendRedirect(googleOauth.getOauthRedirectURL());
                break;
            case "naver":
                response.sendRedirect(naverOauth.getOauthRedirectURL());
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다.");
        }

    }

    public void oauthLogin(String socialLoginType, String code) throws JsonProcessingException, UnsupportedEncodingException {

        if(socialLoginType.equals("kakao")){

            //카카오로부터 받은 액세스 토큰을 받아온다
            String accessToken = kakaoOauth.getKakaoAccessToken(code);
            kakaoOauth.createKakaoUser(accessToken);



        } else if (socialLoginType.equals("google")) {
            ResponseEntity<String> accessTokenResponse = googleOauth.requestAccessToken(code);

            //구글로부터 받은 액세스 토큰을 받아온다
            GoogleOAuth accessToken = googleOauth.getAccessToken(accessTokenResponse);

            //구글로 액세스 토큰을 보내 사용자 정보를 가져온다.
            ResponseEntity<String> userInfo = googleOauth.requestUserInfo(accessToken);

            GoogleUser googleUser = googleOauth.getUserInfo(userInfo);
            log.info(googleUser.toString());

        } else if (socialLoginType.equals("naver")) {
            String accessToken = naverOauth.requestAccessToken(code);
            ResponseEntity<String> userInfo = naverOauth.requestUserInfo(accessToken);


        }else {
            throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
        }

    }








}
