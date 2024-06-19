package com.moadream.giftogether.product.model;
 

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.wishlist.model.WishList;

//import com.moadream.giftogether.wishList.Wishlist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "T_Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", length = 30)
    private String name;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "link", length = 255)
    private String link;
    
    @Column(name = "externa_link", length = 255)
    private String externaLlink;
    
    @Column(name = "product_img", length = 255)
    private String productImg;
    
    @Column(name = "option_detail", length = 120, nullable = true)
    private String optionDetail;
    
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private Status status;
    
    @Column(name = "current_amount", nullable = false)
    private int currentAmount;
    
    @Column(name = "goal_amount", nullable = false)
    private int goalAmount;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateAt;
    
	@ManyToOne
	@JoinColumn(name = "wishlist_id")
	private WishList wishlist; // FK
	// Getters and Setters
	
	@OneToMany(mappedBy = "product")
	private List<Funding> FundingList = new ArrayList<>();

}
