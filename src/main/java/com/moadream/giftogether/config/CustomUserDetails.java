package com.moadream.giftogether.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.model.Role;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Getter
public class CustomUserDetails implements UserDetails {
	
	private Member member;
	
	
	public CustomUserDetails(Member member) {
		log.info("=======userDetails ====  초기화  ");
		this.member = member;
	}
	private GrantedAuthority getAuthority(Role role) {
        return new SimpleGrantedAuthority("ROLE_" + role.name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();

        switch (member.getRole()) {
            case ADMIN : authorityList.add(getAuthority(Role.ADMIN));
            break;
            case MEMBER : authorityList.add(getAuthority(Role.MEMBER));
            break;
        }

        return authorityList;
    }

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getUsername() {
		 // 사용자의 Kakao ID 반환
        return member.getSocialLoginId();
	}
	@Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 체크 로직 구현
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부 체크 로직 구현
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 패스워드 만료 여부 체크 로직 구현
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 체크 로직 
    }

}
