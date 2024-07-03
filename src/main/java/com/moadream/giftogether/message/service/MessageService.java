package com.moadream.giftogether.message.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.repository.FundingRepository;
import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.message.model.MessageFundDto;
import com.moadream.giftogether.message.repository.MessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

	private final MessageRepository messageRepository;
	private final FundingRepository fundingRepository;

	// 메시지 수정
	public void modifyMessage(String fundingUid, String message) {
		Funding funding = fundingRepository.findByFundingUid(fundingUid);
		if (funding != null && funding.getMessage() != null) {
			funding.getMessage().setContent(message);
			fundingRepository.save(funding);  
		} else { 
			throw new IllegalStateException("Funding or Message not found for given UID.");
		}

	}

	@Transactional(readOnly = true)
	public List<MessageFundDto> getMessageFunding(String wishlistLink) {
		List<Message> allByWishlistLink = messageRepository.findAllByWishlist_LinkAndStatus(wishlistLink, Status.A);
		List<MessageFundDto> messageFundDtoList = new ArrayList<>();

		for (Message message : allByWishlistLink) {
			MessageFundDto dto = new MessageFundDto();
			dto.setMemberId(message.getFunding().getMember().getId());
			dto.setName(message.getFunding().getMember().getNickname());
			dto.setContent(message.getContent());
			dto.setAmount(message.getFunding().getAmount());
			dto.setFundingUID(message.getFunding().getFundingUid());

			messageFundDtoList.add(dto);
		}

		return messageFundDtoList;
	}

	@Transactional(readOnly = true)
	public List<MessageFundDto> getMessageFundingProduct(String productLink) {
		List<Funding> fundingList = fundingRepository.findAllByProduct_ProductLinkAndStatus(productLink, Status.A);

		List<MessageFundDto> messageFundDtoList = new ArrayList<>();

		for (Funding funding : fundingList) {
			MessageFundDto dto = new MessageFundDto();
			Message message = funding.getMessage();

			log.info("message = " + message.getId());

			dto.setMemberId(message.getFunding().getMember().getId());
			dto.setName(message.getFunding().getMember().getNickname());
			dto.setContent(message.getContent());
			dto.setAmount(message.getFunding().getAmount());
			dto.setFundingUID(message.getFunding().getFundingUid());

			messageFundDtoList.add(dto);
		}

		return messageFundDtoList;

	}

}
