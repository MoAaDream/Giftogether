package com.moadream.giftogether.member;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moadream.giftogether.Status;
import com.moadream.giftogether.config.CustomUserDetails;
import com.moadream.giftogether.config.CustomUserDetailsService;
import com.moadream.giftogether.funding.model.FundingDetailsDTO;
import com.moadream.giftogether.funding.service.FundingService;
import com.moadream.giftogether.member.model.Friend;
import com.moadream.giftogether.member.model.GetMemberRes;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.model.Role;
import com.moadream.giftogether.member.model.UpdateMemberReq;
import com.moadream.giftogether.member.service.KakaoService;
import com.moadream.giftogether.member.service.MemberService;
import com.moadream.giftogether.wishlist.service.WishListService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MemberController {

	@Autowired
	KakaoService kakaoService;

	@Autowired
	MemberService memberService;

	@Autowired
	FundingService fundingService;
	
	
	@Autowired
	WishListService wishListService;

	@Autowired
	CustomUserDetailsService customUserService;

	/**
	 * 카카오 callback [GET] /oauth/kakao/callback
	 * 
	 */

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String client_id;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirect_uri;

	
	@GetMapping("/main")
	public String mainPage() {
		return "main";
	}
	
	
	
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
		// 1. 인가코드 받기

		// redirect_uri로 인가 코드 전달

		// 2. 인가코드를 기반으로 Access Token 발급
		String accessToken = kakaoService.getAccessTokenFromKakao(code);
		
		
		// 3. 사용자 정보 받기
		// return ResponseEntity.ok(memberService.getKakaoUserInfo(accessToken));
		HashMap<String, Object> kakaoUserInfo = kakaoService.getKakaoUserInfo(accessToken);

		// 4. 사용자 정보에서 kakaoId, nickname, profile_image 추출
		String kakaoId = kakaoUserInfo.get("id").toString();

		// 사용자 정보에서 properties 객체 추출
		Map<String, Object> properties = (Map<String, Object>) kakaoUserInfo.get("properties");

		// properties 객체에서 nickname과 profile_image 추출
		String nickname = properties.get("nickname").toString();
		String profileImage = properties.get("profile_image").toString();

		// 사용자 정보에서 kakao_acount 객체 추출
		Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUserInfo.get("kakao_account");

		String email = kakaoAccount.get("email").toString();

		// 5. 사용자 정보를 세션에 저장
		session.setAttribute("kakaoId", kakaoId);
		session.setAttribute("nickname", nickname);
		session.setAttribute("profileImage", profileImage);
		session.setAttribute("accessToken", accessToken);
		session.setAttribute("email", email); 


		// 6. 세션 유지 시간 설정 
		session.setMaxInactiveInterval(60 * 30); //60초  * 30 ->  30분
		
	
		CustomUserDetails userDetails;

		// 회원가입 여부 확인 및 처리
		if (!memberService.isMemberExists(kakaoId, "Kakao")) {

			// 회원 가입 처리
			Member newMember = new Member();
			newMember.setSocialLoginId(kakaoId);
			newMember.setSocialProvider("Kakao");
			newMember.setNickname(nickname);
			newMember.setProfile(profileImage);
			newMember.setEmail(email);
			newMember.setStatus(Status.A);
			newMember.setRole(Role.MEMBER);
			newMember.setMisbehaviorCount(0);

			// MemberRepository를 사용하여 저장
			memberService.createMember(newMember);
			userDetails = new CustomUserDetails(newMember);

		} else {
			userDetails = (CustomUserDetails) customUserService.loadUserByUsername(kakaoId);
		}

		log.info("==========카카오 로그인 ========= " + userDetails);
		// 로그인 처리
		customUserService.loadUserDirectly(userDetails, session);
		// 홈 화면으로 리다이렉트
		return "redirect:/main";

	}

	@GetMapping("/current-user")
	public String getCurrentUser(Model model, HttpSession session) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			model.addAttribute("currentUser", "사용자가 인증되지 않았습니다.");
			model.addAttribute("sessionUser", "세션에 저장된 사용자 정보가 없습니다.");
		} else if (authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof UserDetails) {
				String username = ((UserDetails) principal).getUsername();
				model.addAttribute("currentUser", "현재 로그인 사용자: " + username);
			} else {
				model.addAttribute("currentUser", "현재 로그인 사용자: " + principal.toString());
			}

			// 세션에서 저장된 정보 가져오기 (예시로 kakaoId를 가져오는 경우)
			String sessionKakaoId = (String) session.getAttribute("kakaoId");
			if (sessionKakaoId != null) {
				model.addAttribute("sessionUser", "세션에 저장된 사용자 (kakaoId): " + sessionKakaoId);
			} else {
				model.addAttribute("sessionUser", "세션에 저장된 사용자 정보가 없습니다.");
			}
		} else {
			model.addAttribute("currentUser", "사용자가 인증되지 않았습니다.");
			model.addAttribute("sessionUser", "세션에 저장된 사용자 정보가 없습니다.");
		}

		return "current-user-page"; // 해당 템플릿 페이지의 이름
	}
	/**
	 * 카카오 로그아웃
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	/*
	@GetMapping("/logout")
	public String logout(HttpSession session ,  HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
		log.info("로그아웃 세션 정보 : " + session.getCreationTime());
		String accessToken = (String) session.getAttribute("accessToken");
		log.info("============ 로그아웃 ===========");
		if (accessToken != null && !accessToken.isEmpty()) {
			
			kakaoService.logoutFromKakao(accessToken);
		} else {
			log.info("No access token found in session.");
		}
		session.invalidate(); // 세션 무효화
		
		// 모든 쿠키 삭제
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue(null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        
		
		log.info("logout 경로 도착");

		return "redirect:/login?logout";
	}*/


	@GetMapping("/logout")
    public RedirectView logout(@RequestParam(name = "state", required = false) String state) {
        if (state == null) {
            state = "defaultState"; // or generate a unique state for CSRF protection
        }

        String logoutUrl = kakaoService.getKakaoLogoutUrl(state);
        return new RedirectView(logoutUrl);
    }

    @GetMapping("/logout/redirect")
    public String handleLogoutRedirect(@RequestParam(name = "state", required = false) String state) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return "redirect:/login"; // Redirect to your home or login page after logout
    }
    
    
	
	 
	/**
	 * 마이페이지
	 * 
	 * @return
	 * @throws Exception
	 */

	@GetMapping("/member/{id}")
	public String getUserInfo(@PathVariable("id") Long id, Model model, HttpSession session) throws Exception {

		log.info("GET /member/{} 요청 도착", id); // 로그 추가
		log.info("마이페이지 접근");

		String kakaoId = (String) session.getAttribute("kakaoId");
		if (kakaoId == null) {
			log.error("세션에 kakaoId가 없습니다.");
			throw new Exception("유효하지 않은 접근입니다");
		}

		Member member = memberService.getMemberInfo(id);

		if (member == null) {
			log.error("해당 ID의 회원 정보를 찾을 수 없습니다. ID: {}", id);
			throw new Exception("유효하지 않은 접근입니다");
		}

		if (!member.getSocialLoginId().equals(kakaoId)) {
			log.error("유효하지 않은 접근입니다. 사용자 ID: {}", id);
			throw new Exception("유효하지 않은 접근입니다");
		}

		GetMemberRes getMemberRes = new GetMemberRes();
		getMemberRes.setId(member.getId());
		getMemberRes.setNickname(member.getNickname());
		getMemberRes.setBirth(member.getBirth());
		getMemberRes.setPhoneNumber(member.getPhoneNumber());
		getMemberRes.setProfile(member.getProfile());
		getMemberRes.setRole(member.getRole());
		getMemberRes.setStatus(member.getStatus());
		getMemberRes.setAddress(member.getAddress());

		model.addAttribute("member", getMemberRes);
		

		model.addAttribute("ongoingWishlists", wishListService.getListsByStatusAndPage(id,Status.A, 0));  // 진행중인 위시리스트 
		model.addAttribute("expiredWishlists" , wishListService.getListsByStatusAndPage(id, Status.I, 0)); // 종료된 위시리스트
		
		return "member/mypage";

	}

	/**
	 * 사용자 정보 수정 페이지
	 * 
	 * @return
	 * @throws Exception
	 */

	@GetMapping("/member/{id}/edit")
	public String editUserInfo(@PathVariable("id") Long id, Model model, HttpSession session) throws Exception {

		log.info("수정페이지 접근");
		String kakaoId = (String) session.getAttribute("kakaoId");

		Member member = memberService.getMemberInfo(id);

		if (member == null) {
			log.info("member가 존재하지 않습니다.");
			// 멤버가 존재하지 않는 경우 에러 페이지로 리다이렉트
			return "redirect:/error";
		}
		if (member.getStatus() == Status.D) {
			log.info("탈퇴한 회원이 다시 로그인 시도");
			return "redirect:/";
		}

		if (!member.getSocialLoginId().equals(kakaoId)) {
			throw new Exception("message : 유효하지 않은 접근입니다");
		}

		UpdateMemberReq updateMemberReq = new UpdateMemberReq();
		updateMemberReq.setNickname(member.getNickname());
		updateMemberReq.setBirth(member.getBirth());
		updateMemberReq.setPhoneNumber(member.getPhoneNumber());
		updateMemberReq.setProfile(member.getProfile());
		updateMemberReq.setAddress(member.getAddress());

		model.addAttribute("member", updateMemberReq);
		model.addAttribute("id", id);
		return "member/edit";
	}

	/**
	 * 사용자 정보 수정
	 * 
	 * @return
	 * @throws Exception
	 */

	@PostMapping("/member/{id}")
	public String updateUserInfo(@PathVariable("id") Long id,
			@Valid @ModelAttribute("member") UpdateMemberReq updateMemberReq, HttpSession session) throws Exception {

		String kakaoId = (String) session.getAttribute("kakaoId");

		Member member = memberService.getMemberInfo(id);

		if (!member.getSocialLoginId().equals(kakaoId)) {
			throw new Exception("message : 유효하지 않은 접근입니다");
		}
		memberService.updateMember(id, updateMemberReq);
		return "redirect:/member/" + id;
	}

	/**
	 * 회원 탈퇴
	 * 
	 * @return
	 * @throws Exception
	 */

	@PostMapping("/member/{id}/d")
	public ResponseEntity<String> softDeleteMember(@PathVariable("id") Long id, HttpSession session) throws Exception {
		String kakaoId = (String) session.getAttribute("kakaoId");

		Member member = memberService.getMemberInfo(id);

		if (!member.getSocialLoginId().equals(kakaoId)) {
			throw new Exception("message : 유효하지 않은 접근입니다");
		}
		try {

			memberService.deleteMember(id);
			return ResponseEntity.ok("탈퇴하였습니다.");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("탈퇴 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
	
	
	/**
	 * 친구 불러오기
	 * 
	 */
	@GetMapping("/member/friends")
    public String getFriends(Model model, HttpSession session) {
		Long memberId = memberService.getMemberBySocialId((String) session.getAttribute("kakaoId")).getId();
        String accessToken = (String) session.getAttribute("accessToken");
        if (accessToken == null) {
            return "redirect:/login";
        }

        model.addAttribute("id", memberId);
        String url = "https://kapi.kakao.com/v1/api/talk/friends";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode friendsList = root.path("elements");

                List<Friend> friends = new ArrayList<>();
                for (JsonNode friendNode : friendsList) {
                    String friendId = friendNode.path("uuid").asText();
                    String profileImage = friendNode.path("profile_thumbnail_image_url").asText();
                    String nickname = friendNode.path("profile_nickname").asText();
                    friends.add(new Friend(friendId, profileImage, nickname));
                }

                model.addAttribute("friends", friends);
                return "member/friendList"; 
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Failed to parse friends list.");
                return "errorPage"; 
            }
        } else {
            model.addAttribute("errorMessage", "Failed to retrieve friends list.");
            return "errorPage"; 
        }
    
	}

	 /* 카카오 동의항목 조회 */
	 @GetMapping("/consent")
	    public ResponseEntity<String> getConsentInfo(HttpSession session) {
		 String scopeInfoUri="https://kapi.kakao.com/v2/user/scopes";	
		 
		 String accessToken = (String) session.getAttribute("accessToken");
	    
		 RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = new HttpHeaders();
	        headers.setBearerAuth(accessToken);

	        HttpEntity<String> entity = new HttpEntity<>(headers);

	        ResponseEntity<String> response = restTemplate.exchange(scopeInfoUri, HttpMethod.GET, entity, String.class);

	        return response;
	    }
	 
	 
	/* 이용중 프로필 , 메세지 동의 구하기 */
    @GetMapping("/request/friends-consent")
    public RedirectView requestFriendsConsent() {
    	
        String consentUri = String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&scope=friends, talk_message",
        		"https://kauth.kakao.com/oauth/authorize", client_id, redirect_uri);
        return new RedirectView(consentUri);
    }

   
    
    @PostMapping("/sendMessage")
    public String message(Model model, HttpSession session) throws JsonProcessingException {
    	String apiUrl = "https://kapi.kakao.com/v1/api/talk/friends/message/default/send";
        String[] receivers = {"0uDR4dbi1eLU-MD2xfDB8cn_0-LS69nh2O2Z"};
        
    	String[] recievers = {};
    	
    	
    	String accessToken = (String) session.getAttribute("accessToken");
        
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 메시지 템플릿 구성 요소
        Map<String, Object> templateObject = new HashMap<>();
        templateObject.put("object_type", "text");
        templateObject.put("text", "Hello, this is a test message.");
        templateObject.put("link", Map.of("web_url", "https://developers.kakao.com"));
        templateObject.put("button_title", "Check this out");

        // JSON 문자열로 변환
        String templateObjectStr = new ObjectMapper().writeValueAsString(templateObject);

        // 수신자 배열을 JSON 배열 문자열로 변환
        String receiversJsonArray = new ObjectMapper().writeValueAsString(receivers);

        // URL 인코딩된 문자열로 변환
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("receiver_uuids", receiversJsonArray);
        requestBody.add("template_object", templateObjectStr);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            model.addAttribute("response", response.getBody());
        } catch (Exception e) {
            model.addAttribute("response", "Error: " + e.getMessage());
        }
        
        log.info("메시지 보내기 성공");
        return "redirect:/member/friends";
    }


    
	@GetMapping("/member/{id}/fundings")
	public String getUserFundings(@PathVariable("id") Long id, Model model, HttpSession session) throws Exception {

		String kakaoId = (String) session.getAttribute("kakaoId");
		if (kakaoId == null) {
			log.error("세션에 kakaoId가 없습니다.");
			throw new Exception("유효하지 않은 접근입니다");
		}

		Member member = memberService.getMemberInfo(id);

		if (member == null) {
			log.error("해당 ID의 회원 정보를 찾을 수 없습니다. ID: {}", id);
			throw new Exception("유효하지 않은 접근입니다");
		}

		if (!member.getSocialLoginId().equals(kakaoId)) {
			log.error("유효하지 않은 접근입니다. 사용자 ID: {}", id);
			throw new Exception("유효하지 않은 접근입니다");
		}

			    
	    
		List<FundingDetailsDTO> fundingDetailM = fundingService.findFundingsBySocialId(kakaoId);
		model.addAttribute("fundingDetailM", fundingDetailM);
//		for (FundingDetailsDTO ff : fundingDetailM) {
//			log.info("ㅁㅁㅁ" + ff.isSuccessFunding()); 
//		}
		return "member/pay_statics";

	}
	



	
	
}