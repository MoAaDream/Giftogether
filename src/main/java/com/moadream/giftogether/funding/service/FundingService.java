package com.moadream.giftogether.funding.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.model.FundingDetailsDTO;
import com.moadream.giftogether.funding.model.Payment;
import com.moadream.giftogether.funding.model.PaymentStatus;
import com.moadream.giftogether.funding.repository.FundingRepository;
import com.moadream.giftogether.funding.repository.PaymentRepository;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.message.repository.MessageRepository;
import com.moadream.giftogether.product.model.Product;
import com.moadream.giftogether.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class FundingService {
	private final FundingRepository fundingRepository;
	private final PaymentRepository paymentRepository;
	private final ProductRepository productRepository;
	private final MemberRepository memberRepository;
	private final MessageRepository messageRepository;
	

	@Transactional 
	public Funding fund(String socialId, String productLink, Integer amount, String messageF) {

		Member member = findMemberBySocialId(socialId);

		// 임시 결제내역 생성
		Payment payment = Payment.builder().amount(amount) // 검증 할 값
				.status(PaymentStatus.R).build();

		paymentRepository.save(payment);

//        log.info("member id = " + member.getId());
//        List<Product> list = productRepository.findAll();
//        for(Product p : list)
//        	log.info("log = " + p.getId());

		Product product = productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with link: " + productLink));

		// 주문 생성
		Funding funding = Funding.builder().amount(amount).status(Status.I).fundingUid(UUID.randomUUID().toString())
				.payment(payment).member(member).product(product).build();

		fundingRepository.save(funding);
		
		// message .save
		Message message = Message.builder().content(messageF).status(Status.I)
				.funding(funding).wishlist(product.getWishlist()).build();
		
		messageRepository.save(message);
		
		return funding;
	}

	public List<FundingDetailsDTO> findFundingsByProductLink(String productLink) {
		return productRepository.findByProductLink(productLink)
				.map(product -> fundingRepository.findByProductIdWithDetails(product.getId()))
				.map(fundings -> fundings.stream().filter(funding -> funding.getStatus() == Status.A)
						.map(this::mapToDTO).collect(Collectors.toList()))
				.orElse(Collections.emptyList());
	}

	public List<FundingDetailsDTO> findFundingsBySocialId(String socialId) {
		List<Funding> fundings = fundingRepository.findByMemberSocialIdWithDetails(socialId);
		return fundings.stream().filter(funding -> funding.getStatus() == Status.A).map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	public FundingDetailsDTO getFundingDetailByUid(String fundingUid) { 
	    Funding funding = fundingRepository.findByFundingUid(fundingUid); 
	    return funding != null ? mapToDTO(funding) : null;
	}

	
	
	private FundingDetailsDTO mapToDTO(Funding funding) {
		FundingDetailsDTO dto = new FundingDetailsDTO();
		dto.setAmount(funding.getAmount());
		dto.setStatus(funding.getStatus());
		dto.setMessageContent(funding.getMessage().getContent() != null ? funding.getMessage().getContent() : null);
		dto.setGiverNickname(funding.getMember().getNickname());
		dto.setReceiverNickname(funding.getProduct().getWishlist().getMember().getNickname());
		dto.setFundingUid(funding.getFundingUid());
		dto.setCreatedAt(funding.getCreatedAt());
		dto.setProductName(funding.getProduct().getName());
		dto.setDeadline(funding.getProduct().getWishlist().getDeadline());
		return dto;
	}

	private Member findMemberBySocialId(String socialId) {
		return memberRepository.findBySocialLoginId(socialId)
				.orElseThrow(() -> new RuntimeException("Member not found for socialId: " + socialId));
	}

}