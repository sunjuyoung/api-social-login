package com.example.securitydemo.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @GetMapping
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello World");
    }


    /**
     * 소셜 로그인
     * kakao or google 로그인 화면으로 리다이렉트
     */
    @GetMapping("/oauth2/login/{socialLoginType}")
    public void socialLogin(@PathVariable("socialLoginType") String socialLoginType) throws IOException {
        log.info(socialLoginType);
        demoService.request(socialLoginType);
    }
    @GetMapping("/oauth2/{socialLoginType}")
    public void socialLoginRedirect(@PathVariable("socialLoginType") String socialLoginType,
                                    @RequestParam("code") String code) throws JsonProcessingException {
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
