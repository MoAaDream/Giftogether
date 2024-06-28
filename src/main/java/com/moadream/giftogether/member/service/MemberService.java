package com.moadream.giftogether.member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.repository.FundingRepository;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.model.UpdateMemberReq;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.repository.WishListRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FundingRepository fundingRepository;
	
	@Autowired
	private WishListRepository wishListRepository;


	
	//회원 가입된 사용자인지 확인
	@Transactional
	public boolean isMemberExists(String socialLoginId, String socialProvider) {
	    Member member = memberRepository.findBySocialLoginIdAndSocialProvider(socialLoginId, socialProvider);       
	    if (member == null) {
	        return false;
	    }
	    if (member.getStatus() == Status.A) {
	        return true;
	    } else if (member.getStatus() == Status.D) {
	        member.setStatus(Status.A);
	        memberRepository.save(member); 
	        return true;
	    }
	    return false;
	}
	
	
	@Transactional
	public void createMember(Member newMember) {
		Member savedMember = memberRepository.save(newMember);
	}
	

	
	// 사용자 정보 가져오기
	public Member getMemberInfo(Long id) {
		Member member =  memberRepository.findMemberByIdAndStatus(id,Status.A);
		log.info(id + "번째 member의 정보 가져오는중");
		return member;
	}
	
	
	// 사용자 정보 social id로 가져오기
	public Member getMemberBySocialId(String id) {
		Member member = memberRepository.findBySocialLoginIdAndSocialProvider( id, "Kakao");
		log.info("카카오 아이디로 계정 정보 가져오기");
		return member;
	}
	
	

	public void updateMember(Long id, UpdateMemberReq updateMemberReq) {
		Member member = memberRepository.findMemberByIdAndStatus(id, Status.A);
	
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


	// 탈퇴
	public void deleteMember(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(() ->
        new RuntimeException("Member not found"));

		List<Funding> fundingList = fundingRepository.findAllByMember_Id(memberId);
		for(Funding funding :fundingList) {
			funding.setStatus(Status.D);
		}
		List<WishList> allWishListByMember = wishListRepository.findAllByMember_Id(memberId);
		wishListRepository.deleteAll(allWishListByMember);
		
	
		member.setStatus(Status.D);
		memberRepository.save(member);
	}
	
	

}	
