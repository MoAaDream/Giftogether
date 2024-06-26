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
import java.util.UUID;

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
	private List<Message> messageList = new ArrayList<>();

	@OneToMany(mappedBy = "wishlist")
	private List<Product> productList = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;


	public static WishList createWishList(WishListForm wishListForm, Member member){
		WishList wishList = new WishList();
		wishList.link = UUID.randomUUID().toString().replace("-", "");
		wishList.name = wishListForm.getName();
		wishList.description = wishListForm.getDescription();

		if(wishListForm.getUploadedImage() == null || wishListForm.getUploadedImage().equals(""))
			wishListForm.setUploadedImage("https://moadreambk.s3.ap-northeast-2.amazonaws.com/wishlists/wishlist.png");

		wishList.listImg = wishListForm.getUploadedImage();

		wishList.address = wishListForm.getAddress();

		wishList.phoneNumber = wishListForm.getPhoneNumber();

		wishList.deadline = wishListForm.getDeadLine();

		wishList.status = Status.A;

		wishList.member = member;

		return wishList;
	}

	public void modifyWishList(WishListForm wishListForm){
		this.name = wishListForm.getName();
		this.description = wishListForm.getDescription();
		this.listImg = wishListForm.getUploadedImage();
		this.address = wishListForm.getAddress();
		this.phoneNumber = wishListForm.getPhoneNumber();
	}

}
