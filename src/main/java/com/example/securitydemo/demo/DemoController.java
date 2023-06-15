package com.example.securitydemo.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello World");
    }




    /**
     * 소셜 로그인
     * kakao or google 로그인 요청
     */
    @GetMapping("/oauth2/login/{socialLoginType}")
    public void socialLogin(@PathVariable("socialLoginType") String socialLoginType) throws IOException {
        log.info(socialLoginType);
        demoService.request(socialLoginType);
    }

    /**
     * 소셜 로그인 토큰, 유저 정보 받기
     * @param socialLoginType
     * @param code
     * @throws JsonProcessingException
     */
    @GetMapping("/oauth2/{socialLoginType}")
    public void socialLoginRedirect(@PathVariable("socialLoginType") String socialLoginType,
                                    @RequestParam("code") String code) throws JsonProcessingException, UnsupportedEncodingException {
        log.info(code);
        log.info(socialLoginType);
        demoService.oauthLogin(socialLoginType, code);
    }

//    /**
//     * 카카오 로그인
//     */
//    @GetMapping("/oauth2/kakao")
//    public void kakaoCallback(@RequestParam("code") String code) {
//        log.info(code);
//        String kakaoAccessToken = demoService.getKakaoAccessToken(code);
//        demoService.createKakaoUser(kakaoAccessToken);
//    }



}
