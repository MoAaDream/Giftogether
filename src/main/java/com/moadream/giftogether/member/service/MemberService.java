package com.moadream.giftogether.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

	public void updateMember(Long id, UpdateMemberReq newMemberInfo) {
		Member member = memberRepository.findMemberById(id);
	
		if (newMemberInfo.getNickname() != null) {
	        member.setNickname(newMemberInfo.getNickname());
	    }
	    if (newMemberInfo.getProfile() != null) {
	        member.setProfile(newMemberInfo.getProfile());
	    }
	    if (newMemberInfo.getBirth() != null) {
	        member.setBirth(newMemberInfo.getBirth());
	    }
	    if (newMemberInfo.getAddress() != null) {
	        member.setAddress(newMemberInfo.getAddress());
	    }
	    if (newMemberInfo.getPhoneNumber() != null) {
	    	member.setPhoneNumber(newMemberInfo.getPhoneNumber());
	    }
	    memberRepository.save(member);

	}

	public String uploadProfileImage(MultipartFile profileImage) {
		 // 프로필 이미지를 업로드하고, 업로드된 이미지의 URL을 반환하는 로직 구현 (예: AWS S3에 업로드)
        // 실제 구현은 여기서 처리
		return null;
	}
}	
