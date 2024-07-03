package com.moadream.giftogether.member.service;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.moadream.giftogether.member.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {


	@Autowired
	private final MemberRepository memberRepository;
	 
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;
	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirectUri;
	
	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String clientSecret;
	
	// 인가코드 반환
	public String getAccessTokenFromKakao(String code) throws JsonProcessingException {

		 // HTTP Header 생성
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	        // HTTP Body 생성
	        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
	        body.add("grant_type", "authorization_code");
	        body.add("client_id", clientId);
	        body.add("redirect_uri", redirectUri);
	        body.add("code", code);
	        body.add("client_secret", clientSecret);

	        // HTTP 요청 보내기
	        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
	        RestTemplate rt = new RestTemplate();
	        ResponseEntity<Map> response = rt.postForEntity("https://kauth.kakao.com/oauth/token", kakaoTokenRequest, Map.class);

	        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
	        String accessToken = response.getBody().get("access_token").toString();

	        return accessToken;
	    }

	//사용자 정보 반환
	public HashMap<String, Object> getKakaoUserInfo(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();
         
         HttpHeaders headers = new HttpHeaders();
         headers.add("Authorization", "Bearer " + accessToken);
         
         HttpEntity<String> entity = new HttpEntity<>("", headers);
         ResponseEntity<Map> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, entity, Map.class);
         
         HashMap<String, Object > userInfo = (HashMap<String, Object>) response.getBody();
         return userInfo;
	}
	
	
	
	// 로그아웃
	//토큰 만료
	public void logoutFromKakao(String accessToken) throws JsonProcessingException{
		 String logoutUrl = "https://kapi.kakao.com/v1/user/logout";
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "Bearer " + accessToken);
	        
	        HttpEntity<String> entity = new HttpEntity<>("", headers);
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.exchange(logoutUrl, HttpMethod.POST, entity, String.class);
	        
	        if (response.getStatusCode().is2xxSuccessful()) {
	            log.info("Successfully logged out from Kakao");
	        } else {
	        	log.info("Failed to log out from Kakao: " + response.getStatusCode());
	        }
	}
	

	

	
	
}
