package com.moadream.giftogether.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.service.KakaoService;
import com.moadream.giftogether.member.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MemberController {

	@Autowired
	KakaoService kakaoService;

	@Autowired
	MemberService memberService;

	/**
	 * 카카오 callback [GET] /oauth/kakao/callback
	 * 
	 */

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String client_id;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirect_uri;

	// 로그인 요청
	@GetMapping("/login")
	public String loginPage(Model model) {
		String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + client_id
				+ "&redirect_uri=" + redirect_uri;
		model.addAttribute("location", location);
		return "login";
	}

	// 로그인 콜백
	@GetMapping("/login/oauth2/code/kakao")
	public String kakaoLogin(@RequestParam("code") String code, HttpSession session) throws JsonProcessingException {
		// SETP1 : 인가코드 받기
		// (카카오 인증 서버는 서비스 서버의 Redirect URI로 인가 코드를 전달합니다.)

		// STEP2: 인가코드를 기반으로 토큰(Access Token) 발급
		String accessToken = kakaoService.getAccessTokenFromKakao(code);

		// STEP3: 사용자 정보 받기
		// return ResponseEntity.ok(memberService.getKakaoUserInfo(accessToken));
		HashMap<String, Object> userInfo = kakaoService.getKakaoUserInfo(accessToken);

		// STEP4: 사용자 정보에서 kakaoId, nickname, profile_image 추출
		String kakaoId = userInfo.get("id").toString();
		// 사용자 정보에서 properties 객체 추출
	    Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
	    
	    // properties 객체에서 nickname과 profile_image 추출
	    String nickname = properties.get("nickname").toString();
	    String profileImage = properties.get("profile_image").toString();

		
		 // 사용자 정보를 세션에 저장
	    session.setAttribute("kakaoId", kakaoId);
	    session.setAttribute("nickname", nickname);
	    session.setAttribute("profileImage", profileImage);
	    
	    // STEP5 : 회원가입 여부 확인 및 처리
	    if (!memberService.isMemberExists(kakaoId, "Kakao")) {
	        // 회원 가입 처리
	        Member newMember = new Member();
	        newMember.setSocialLoginId(kakaoId);
	        newMember.setSocailProvider("Kakao");
	        newMember.setNickname(nickname);
	        newMember.setProfile(profileImage);
	        
	        // MemberRepository를 사용하여 저장
	        memberService.createMember(newMember);
	    }
	    
	    // 홈 화면으로 리다이렉트
	    return "redirect:/home";
		}

	@GetMapping("/home")
	public String home(Model model, HttpSession session) {
		// 세션에서 사용자 이름 가져오기
		String nickname = (String) session.getAttribute("nickname");
		model.addAttribute("name", nickname);
		return "home";
	}

	
	
	/**
	 * 사용자 정보 수정
	 * @return
	 */

	
	/**
	 * 회원 탈퇴
	 * @return 
	 */

	
	/**
	 * 마이페이지
	 * @return
	 */
	
}
