package com.moadream.giftogether.member.model;

import java.util.Date;

import com.moadream.giftogether.Status;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMemberRes {
    private Long id;
    
    @Column(length = 30, nullable = false)
    private String nickname; // 카카오 계정에 설정한 닉네임 

    // 이미지 처리
    private String profile;

    private Date birth;
    
    private String phoneNumber;

    private String address;
    private Role role;

    private Status status;
}
