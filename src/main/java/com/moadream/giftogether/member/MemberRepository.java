package com.moadream.giftogether.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moadream.giftogether.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	
	//id로 Member 찾기
	Member findMemberById(Long id);
	
	//소셜 로그인 아이디로 회원 찾기
    Member findBySocialLoginIdAndSocialProvider(String socialLoginId, String socialProvider);

    Optional<Member> findBySocialLoginId(String socialLoginId);
    
    Optional<Member> findById(Long id);
    
    
}


