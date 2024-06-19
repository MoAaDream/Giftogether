package com.moadream.giftogether.message.model;

import java.time.LocalDateTime;
//
//import com.moadream.giftogether.funding.Funding;
//import com.moadream.giftogether.wishList.Wishlist;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.wishlist.model.WishList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "T_Message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = true)
    private String content;

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private Status status;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateAt;

    
    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private WishList wishlist;

    @OneToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private Funding funding;
    
    // Getters and Setters
}
