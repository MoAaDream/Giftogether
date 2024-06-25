package com.moadream.giftogether.message;

import java.util.List;

import org.springframework.stereotype.Service;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.wishlist.model.WishList;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MessageService {

	private final MessageRepository messageRepository;
	//private final FundingRepository fundingRepository;

	public void create(WishList wishlist, int amount, String content) {
		Message message = new Message();
		Funding funding = new Funding();
		message.setContent(content);
		funding.setAmount(amount);
		message.setWishlist(wishlist);
		message.setFunding(funding);
		message.setStatus(Status.A);
		this.messageRepository.save(message);
		//this.fundingRepository.save(funding);
	}
	
	public List<Message> getList() {

		return this.messageRepository.findAll();
	}

}