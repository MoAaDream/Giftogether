package com.moadream.giftogether.member;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moadream.giftogether.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	//소셜 로그인 아이디로 회원 찾기
    Member findBySocialLoginIdAndSocailProvider(String socialLoginId, String socialProvider);
}
