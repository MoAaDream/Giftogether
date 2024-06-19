package com.moadream.giftogether.message.model;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.global.audit.BaseTimeEntity;
import com.moadream.giftogether.wishlist.model.WishList;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "T_Message")
public class Message extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private WishList wishlist;

    @OneToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private Funding funding;

    // Getters and Setters
}
