package com.moadream.giftogether.funding;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moadream.giftogether.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}