package com.moadream.giftogether.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.moadream.giftogether.Status;
import com.moadream.giftogether.config.CustomUserDetails;
import com.moadream.giftogether.config.CustomUserDetailsService;
import com.moadream.giftogether.member.model.GetMemberRes;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.model.Role;
import com.moadream.giftogether.member.model.UpdateMemberReq;
import com.moadream.giftogether.member.service.KakaoService;
import com.moadream.giftogether.member.service.MemberService;

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
	CustomUserDetailsService customUserService;

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
		
		
		//사용자 정보에서 kakao_acount 객체 추출
		Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUserInfo.get("kakao_account");
		
		String email = kakaoAccount.get("email").toString();

		
		
		// 5. 사용자 정보를 세션에 저장
		session.setAttribute("kakaoId", kakaoId);
		session.setAttribute("nickname", nickname);
		session.setAttribute("profileImage", profileImage);
		session.setAttribute("accessToken", accessToken);
		session.setAttribute("email", email); 
		
		
		
		// 6. 세션 유지 시간 설정 
		session.setMaxInactiveInterval(60 * 30); // 30분
		
		
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

	@GetMapping("/home")
	public String home(Model model, HttpSession session) {
		// 세션에서 사용자 이름 가져오기
		String nickname = (String) session.getAttribute("nickname");
		model.addAttribute("name", nickname);
		Long id = memberService.getMemberBySocialId((String) session.getAttribute("kakaoId")).getId();
		model.addAttribute("id", id);
		/*
		 * Authentication authentication =
		 * SecurityContextHolder.getContext().getAuthentication();
		 * 
		 * if (authentication == null || !authentication.isAuthenticated()) {
		 * model.addAttribute("currentUser", "사용자가 인증되지 않았습니다.");
		 * model.addAttribute("sessionUser", "세션에 저장된 사용자 정보가 없습니다."); } else { Object
		 * principal = authentication.getPrincipal();
		 * 
		 * if (principal instanceof UserDetails) { String username = ((UserDetails)
		 * principal).getUsername(); model.addAttribute("currentUser", "현재 로그인 사용자: " +
		 * username); } else { model.addAttribute("currentUser", "현재 로그인 사용자: " +
		 * principal.toString()); }
		 * 
		 * // 세션에서 저장된 정보 가져오기 (예시로 kakaoId를 가져오는 경우) String sessionKakaoId = (String)
		 * session.getAttribute("kakaoId"); if (sessionKakaoId != null) {
		 * model.addAttribute("sessionUser", "세션에 저장된 사용자 (kakaoId): " +
		 * sessionKakaoId); } else { model.addAttribute("sessionUser",
		 * "세션에 저장된 사용자 정보가 없습니다."); } }
		 */
		return "home";
	}
	
	
	@GetMapping("/main")
	public String main(HttpSession session) {
		return "main";
	}

	/**
	 * 카카오 로그아웃
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) throws JsonProcessingException {
		String accessToken = (String) session.getAttribute("accessToken");
		log.info("============ 로그아웃 ===========");
		if (accessToken != null && !accessToken.isEmpty()) {
			log.info("Access token found : " + accessToken);
			kakaoService.logoutFromKakao(accessToken);
		} else {
			log.info("No access token found in session.");
		}
		session.invalidate(); // 세션 무효화
		log.info("logout 경로 도착");

		  return "redirect:/login?logout";
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
	
	
	

}