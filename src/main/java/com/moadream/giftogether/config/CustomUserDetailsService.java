package com.moadream.giftogether.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {
		log.info("=======userDetails ====   loadUserByUsername 실행 ");
		// 사용자를 데이터베이스에서 조회하고 
		Member member = memberRepository.findBySocialLoginId(socialId)
				.orElseThrow(()-> new UsernameNotFoundException("존재하지 않는 회원입니다."));
		
	
		return new CustomUserDetails(member);
	}


	public void loadUserDirectly(CustomUserDetails userDetails, HttpSession session) {
		Authentication authentication =
	            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

	    SecurityContext securityContext = SecurityContextHolder.getContext();
	    securityContext.setAuthentication(authentication);

	    session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		
	}

	


}
