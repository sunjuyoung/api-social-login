package com.example.securitydemo.demo.social.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseSocialOAuth {

    private String jwtToken;
    private int user_num;
    private String accessToken;
    private String tokenType;

}
