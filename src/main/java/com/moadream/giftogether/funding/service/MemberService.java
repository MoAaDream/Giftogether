package com.moadream.giftogether.funding.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.model.Role;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	// 회원 자동 생성
	public Member autoRegister() {
		Member member = Member.builder().username("aaaa").socialLoginId("aaaa")
				.socailProvider("aaaa").nickname("aaaa").role(Role.A).status(Status.A).address("서울특별시 서초구 역삼동").build();

		return memberRepository.save(member);
	} 
	 
	public Member findById(Long memberId) {
	    return memberRepository.findById(memberId)
	            .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
	}

}