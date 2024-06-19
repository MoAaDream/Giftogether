package com.moadream.giftogether.wishlist.model;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.global.audit.BaseTimeEntity;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.product.model.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "T_WishList")
public class WishList extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String link;

	@Column(nullable = false, length = 30)
	private String name;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String listImg;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false, length = 30)
	private String phoneNumber;

	private LocalDateTime deadline;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@OneToMany(mappedBy = "wishlist")
	private List<Message> MessagesList = new ArrayList<>();

	@OneToMany(mappedBy = "wishlist")
	private List<Product> ProductsList = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

}
