package com.moadream.giftogether.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.model.Role;
import com.moadream.giftogether.member.model.UpdateMemberReq;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
	
	// 탈퇴
	
	// 사용자 정보 가져오기
	public Member getMemberInfo(Long id) {
		Member member =  memberRepository.findMemberById(id);
		log.info(id + "번째 member의 정보 가져오는중");
		return member;
	}

	public void updateMember(Long id, UpdateMemberReq updateMemberReq) {
		Member member = memberRepository.findMemberById(id);
	
		if (updateMemberReq.getNickname() != null) {
	        member.setNickname(updateMemberReq.getNickname());
	    }
	    if (updateMemberReq.getProfile() != null) {
	    	log.info(updateMemberReq.getProfile());
	        member.setProfile(updateMemberReq.getProfile());
	    }
	    if (updateMemberReq.getBirth() != null) {
	        member.setBirth(updateMemberReq.getBirth());
	    }
	    if (updateMemberReq.getAddress() != null) {
	        member.setAddress(updateMemberReq.getAddress());
	    }
	    if (updateMemberReq.getPhoneNumber() != null) {
	    	member.setPhoneNumber(updateMemberReq.getPhoneNumber());
	    }
	    memberRepository.save(member);

	}


}	
