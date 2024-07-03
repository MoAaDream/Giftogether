package com.moadream.giftogether.wishlist.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import lombok.Getter;

@Getter
public class WishlistDto {

	private String link;
	private String name;
	private String description;
	private String listImg;
	private String address;
	private String phoneNumber;
	private String status;
	private String dDay;

	public WishlistDto(WishList wishList) {
		this.link = wishList.getLink();
		this.name = wishList.getName();
		this.description = wishList.getDescription();
		this.listImg = wishList.getListImg();
		this.address = wishList.getAddress();
		this.phoneNumber = wishList.getPhoneNumber();
		this.status = wishList.getStatus().name();

		LocalDate deadlineDate = wishList.getDeadline().toLocalDate();
		long dDayValue = ChronoUnit.DAYS.between(LocalDate.now(), deadlineDate);
		this.dDay = "D-" + dDayValue;

		if (dDayValue < 0)
			this.dDay = "모금종료";
	}
}
