package com.moadream.giftogether.wishlist.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.product.model.Product;

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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "T_WishList")
public class WishList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String link;

	@Column(nullable = false, length = 30)
	private String name;

	@Column(nullable = false, length = 255)
	private String description;

	@Column(nullable = false, length = 255)
	private String listimg;

	@Column(nullable = false, length = 255)
	private String address;

	@Column(nullable = false, length = 30)
	private String phonenumber;

	private LocalDateTime deadline;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	private LocalDateTime createAt;

	private LocalDateTime updateAt;

	@OneToMany(mappedBy = "wishlist")
	private List<Message> MessagesList = new ArrayList<>();

	@OneToMany(mappedBy = "wishlist")
	private List<Product> ProductsList = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

}
