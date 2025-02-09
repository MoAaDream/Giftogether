package com.moadream.giftogether.member;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	
	Optional<Member> findById(Long id);
	
	//id로 Member 찾기
	Member findMemberByIdAndStatus(Long id, Status status);
	
	//소셜 로그인 아이디로 회원 찾기
    Member findBySocialLoginIdAndSocialProvider(String socialLoginId, String socialProvider);

    Optional<Member> findBySocialLoginId(String socialLoginId);

	List<Member> findAllByMisbehaviorCountGreaterThanEqual(int misbehaviorCount);

    
    
}


