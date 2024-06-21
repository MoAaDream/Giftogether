package com.moadream.giftogether.member.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.bank.model.Bank;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.global.audit.BaseTimeEntity;
import com.moadream.giftogether.wishlist.model.WishList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "T_Member")
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable =false)
    private String socialLoginId;
    
    @Column(nullable =false)
    private String socialProvider;
    
    /*
    @Column(length = 30, nullable = false)
    private String username; // 실명 
*/
    @Column(length = 30, nullable = false)
    private String nickname; // 카카오 계정에 설정한 닉네임 

    // 이미지 처리
    private String profile;

    private Date birth;

    @Column(unique = true, length = 50)
    private String phoneNumber;

    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "member")
    private List<Funding> fundingLists = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<WishList> wishLists = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Bank> bankLists = new ArrayList<>();

}