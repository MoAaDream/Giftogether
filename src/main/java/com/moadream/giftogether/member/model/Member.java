package com.moadream.giftogether.member.model;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.bank.model.Bank;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.global.audit.BaseTimeEntity;
import com.moadream.giftogether.wishlist.model.WishList;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "T_Member")
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //설명
    @Column(length = 30, nullable = false)
    private String username;

    @Column(length = 30, nullable = false)
    private String nickname;

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
