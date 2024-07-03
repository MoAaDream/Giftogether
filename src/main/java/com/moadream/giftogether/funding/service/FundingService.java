package com.moadream.giftogether.funding.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.DataNotFoundException;
import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.model.FundingDetailsDTO;
import com.moadream.giftogether.funding.model.FundingRequest;
import com.moadream.giftogether.funding.model.Payment;
import com.moadream.giftogether.funding.model.PaymentStatus;
import com.moadream.giftogether.funding.repository.FundingRepository;
import com.moadream.giftogether.funding.repository.PaymentRepository;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.message.repository.MessageRepository;
import com.moadream.giftogether.product.Repository.ProductRepository;
import com.moadream.giftogether.product.model.Product;

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

	@Autowired
	private FundingQueueManager fundingQueueManager; // FundingQueueManager 주입

	public void enqueueFundingRequest(FundingRequest request) {
		fundingQueueManager.addFundingRequest(request);
	}

	public int[] getFundingAmounts(String productLink) {
		// 제품을 찾아 없으면 예외 발생
		Product product = productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with link: " + productLink));

		// 남은 모금 필요 금액 계산
		int remainingAmount = product.getGoalAmount() - product.getCurrentAmount();

		// 기본 펀딩 옵션 설정
		int[] defaultOptions = { 5000, 10000, 20000, 30000, 40000, 50000 };

		// 남은 모금 필요 금액보다 작은 펀딩 옵션만 필터링
		List<Integer> filteredOptions = Arrays.stream(defaultOptions).filter(amount -> amount <= remainingAmount)
				.boxed().collect(Collectors.toList());

		// 남은 모금 필요 금액을 목록의 맨 끝에 추가 (길이가 6 미만일 경우에만)
		if (filteredOptions.size() < 6) {
			filteredOptions.add(remainingAmount);
		}

		// 결과를 int 배열로 변환하여 반환
		return filteredOptions.stream().mapToInt(i -> i).toArray();
	}

	public int getInsufficientAmount(String productLink) {
		// 제품을 찾아 없으면 예외 발생
		Product product = productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with link: " + productLink));

		// 남은 모금 필요 금액 계산
		return product.getGoalAmount() - product.getCurrentAmount();
	}

	// 금액 선택 검증
	public void validateAmount(Integer amount, String productLink) throws IllegalArgumentException {

		// 제품을 찾아 없으면 예외 발생
		Product product = productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with link: " + productLink));

		// 남은 모금 필요 금액 계산
		int remainingAmount = product.getGoalAmount() - product.getCurrentAmount();

		if (amount > remainingAmount) {
			throw new IllegalArgumentException("금액이 허용 금액을 초과하여 설정 되었습니다.");
		}
	}

//	public Funding fund(String socialId, String productLink, Integer amount, String messageF) {
	public void processFundingRequest(FundingRequest request) {
		String socialId = request.getSocialId();
		String productLink = request.getProductLink();
		Integer amount = request.getAmount();
		String messageF = request.getMessage();

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

//		 주문 생성
		Funding funding = Funding.builder().amount(amount).status(Status.I).fundingUid(UUID.randomUUID().toString())
				.payment(payment).member(member).product(product).build();
		fundingRepository.save(funding);
		request.setFundingUid(funding.getFundingUid());

		Message message = Message.builder().content(messageF).status(Status.I).funding(funding)
				.wishlist(product.getWishlist()).build();

		messageRepository.save(message);
	}

//    
//	public List<FundingDetailsDTO> findFundingsByProductLink(String socialId, String productLink) {
//		return productRepository.findByProductLink(productLink)
//				.map(product -> fundingRepository.findByProductIdWithDetails(product.getId()))
//				.map(fundings -> fundings.stream().filter(funding -> funding.getStatus() == Status.A)
//						.map(this::mapToDTO).collect(Collectors.toList()))
//				.orElse(Collections.emptyList());
//	}

	public String getProductLinkByFundingUid(String fundingUid) {
		Funding funding = fundingRepository.findByFundingUid(fundingUid);
		return funding.getProduct().getProductLink();
	}

	public List<FundingDetailsDTO> findFundingsByProductLink(String socialId, String productLink) {
		return productRepository.findByProductLink(productLink)
				.map(product -> fundingRepository.findByProductIdWithDetails(product.getId()))
				.map(fundings -> fundings.stream().filter(funding -> funding.getStatus() == Status.A)
						.map(funding -> mapToDTO(funding, socialId)) // socialId를 전달
						.collect(Collectors.toList()))
				.orElse(Collections.emptyList());
	}

	public List<FundingDetailsDTO> findFundingsBySocialId(String socialId) {
		List<Funding> fundings = fundingRepository.findByMemberSocialIdWithDetails(socialId);
		return fundings.stream().filter(funding -> funding.getStatus() == Status.A)
				.map(funding -> mapToDTO(funding, socialId)).collect(Collectors.toList());
	}

	public FundingDetailsDTO getFundingDetailByUid(String fundingUid, String socialId) {
		Funding funding = fundingRepository.findByFundingUid(fundingUid);
		return funding != null ? mapToDTO(funding, socialId) : null;
	}

	public String getProductName(String productLink) {
		return productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new DataNotFoundException("제품이 없습니다.")).getName();
	}

	public String getProductDescription(String productLink) {
		return productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new DataNotFoundException("제품 설명이 없습니다.")).getDescription();
	}

	public String getProductProductImg(String productLink) {
		return productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new DataNotFoundException("제품 설명이 없습니다.")).getProductImg();
	}

	private FundingDetailsDTO mapToDTO(Funding funding, String socialId) {
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
		dto.setCanViewDetails(funding.getMember().getSocialLoginId().equals(socialId));
		dto.setSuccessFunding(isSuccessFunding(funding)); // 날짜 지났고 모금액 도달 => true
		LocalDate deadlineDate = funding.getProduct().getWishlist().getDeadline().toLocalDate();
		long dDayValue = ChronoUnit.DAYS.between(LocalDate.now(), deadlineDate);
		dto.setDDay("D - " + dDayValue);
		if (dDayValue == 0)
			dto.setDDay("D - day");
		else
			dto.setDDay("D + " + Math.abs(dDayValue));
		return dto;
	}

	// 날짜 지났고 모금액 도달 => true
	public boolean isSuccessFunding(Funding funding) {
		String productLink = funding.getProduct().getProductLink();
		if (isDeadFunding(productLink) && isGoalAmount(productLink)) {
			return true;
		}
		return false;
	}

	public boolean isDeadFunding(String productLink) {
		Product product = productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with link: " + productLink));
		// 제품의 상태가 Status.I인 경우 true를 반환
		if (product.getStatus() == Status.I && product.getStatus() != Status.D) {
			return true;
		}
		return false;
	}

	public boolean isGoalAmount(String productLink) {
		Product product = productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with link: " + productLink));
		// 모금액 미달 false
		if (product.getCurrentAmount() < product.getGoalAmount()) {
			return false;
		}
		return true;
	}

	public boolean isUserProduct(String socialId, String productLink) {
		// 제품 연결된 회원 찾기
		Optional<Member> memberOpt = productRepository.findMemberByProductLink(productLink);
		// 찾은 회원의 소셜 ID가 입력받은 socialId와 일치하는지 확인
		if (memberOpt.isPresent()) {
			Member member = memberOpt.get();
			return member.getSocialLoginId().equals(socialId);
		}
		// 회원 정보가 없거나, ID가 일치하지 않으면 false 반환
		return false;
	}

	private Member findMemberBySocialId(String socialId) {
		return memberRepository.findBySocialLoginId(socialId)
				.orElseThrow(() -> new RuntimeException("Member not found for socialId: " + socialId));
	}

}