package com.moadream.giftogether.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.model.Role;

@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;
	
	// 회원 가입된 사용자인지 확인
	public boolean isMemberExists(String socialLoginId, String socialProvider) {
        Member member = memberRepository.findBySocialLoginIdAndSocialProvider(socialLoginId, socialProvider);
        return member != null;
    }

	public void createMember(Member newMember) {
		newMember.setStatus(Status.A);
		newMember.setRole(Role.MEMBER);

		memberRepository.save(newMember);
	}
	

	
	// 사용자 정보 가져오기
	public Member getMemberInfo(Long id) {
		return memberRepository.findMemberById(id);
	}
	
	
	// 탈퇴
	public void deleteMember(Long id) {
		Member member = memberRepository.findById(id).orElseThrow(() ->
        new RuntimeException("Member not found"));
		memberRepository.deleteById(id);
	}
	
	
	public void softDeleteMember(Long id) {
		Member member = memberRepository.findById(id).orElseThrow(() ->
        new RuntimeException("Member not found"));
		member.setStatus(Status.D);
		memberRepository.save(member);
	}
}	
