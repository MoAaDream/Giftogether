package com.moadream.giftogether.product.model;


import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.global.audit.BaseTimeEntity;
import com.moadream.giftogether.wishlist.model.WishList;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "T_Product")
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String name;

    private String description;

    private String productLink;

    private String externalLink;

    private String productImg;

    @Column(length = 120)
    private String optionDetail;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private int currentAmount;

    @Column(nullable = false)
    private int goalAmount;

    @ManyToOne
    @JoinColumn(name = "wishlist_id")
    private WishList wishlist;

    @OneToMany(mappedBy = "product")
    private List<Funding> FundingList = new ArrayList<>();

}
