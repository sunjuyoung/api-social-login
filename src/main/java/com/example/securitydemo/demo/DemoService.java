package com.example.securitydemo.demo;

import com.example.securitydemo.demo.social.GoogleOauth;
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
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Log4j2
@Service
@RequiredArgsConstructor
public class DemoService {

    @Value("${oauth2.kakao.clientId}")
    private String kakaoClientId;

    @Value("${oauth2.kakao.secretId}")
    private String kakaoSecretId;



    private final GoogleOauth googleOauth;
    private final HttpServletResponse response;

    public void request(String socialLoginType) throws IOException {
        if(socialLoginType.equals("kakao")){

        } else if (socialLoginType.equals("google")) {
            response.sendRedirect(googleOauth.getOauthRedirectURL());
        }else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다.");
        }


    }
    public void oauthLogin(String socialLoginType, String code) throws JsonProcessingException {

        if(socialLoginType.equals("kakao")){



        } else if (socialLoginType.equals("google")) {
            ResponseEntity<String> accessTokenResponse = googleOauth.requestAccessToken(code);

            //구글로부터 받은 액세스 토큰을 받아온다
            GoogleOAuth accessToken = googleOauth.getAccessToken(accessTokenResponse);

            //구글로 액세스 토큰을 보내 사용자 정보를 가져온다.
            ResponseEntity<String> userInfo = googleOauth.requestUserInfo(accessToken);

            GoogleUser googleUser= googleOauth.getUserInfo(userInfo);

            log.info(googleUser.toString());

        }else {
            throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
        }

    }


    public String getKakaoAccessToken(String code){
        String access_Token="";
        String refresh_Token ="";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id="+kakaoClientId);
            sb.append("&redirect_uri=http://localhost:8081/api/v1/oauth2/kakao");
            sb.append("&client_secret="+kakaoSecretId);
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }


    public void createKakaoUser(String token) {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            Long id = element.getAsJsonObject().get("id").getAsLong();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if (hasEmail) {
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            System.out.println("id : " + id);
            System.out.println("email : " + email);

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
