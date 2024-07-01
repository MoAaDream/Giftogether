package com.moadream.giftogether.funding.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.moadream.giftogether.DataNotFoundException;
import com.moadream.giftogether.product.Repository.ProductRepository;
import com.moadream.giftogether.product.model.Product;
import com.moadream.giftogether.wishlist.exception.WishListException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.model.PaymentCallbackRequest;
import com.moadream.giftogether.funding.model.PaymentStatus;
import com.moadream.giftogether.funding.model.RequestPayDto;
import com.moadream.giftogether.funding.repository.FundingRepository;
import com.moadream.giftogether.funding.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;

import static com.moadream.giftogether.wishlist.exception.WishlistExceptionCode.NOT_DEADLINE_AFTER;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

	private final FundingRepository fundingRepository;
	private final PaymentRepository paymentRepository;
	private final ProductRepository productRepository;
	private final IamportClient iamportClient;

	public RequestPayDto findRequestDto(String fundingUid) {

		Funding fund = fundingRepository.findFundingAndPaymentAndMember(fundingUid)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

		return RequestPayDto.builder().buyerAddress(fund.getMember().getAddress())
				.paymentAmount(fund.getPayment().getAmount()).fundingUid(fund.getFundingUid()).productLink(fund.getProduct().getProductLink()).build();
	}

	public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {

		try {
			// 결제 단건 조회(아임포트) payment 페이지에서 결제 했음
			IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
			// 주문내역 조회
			Funding fund = fundingRepository.findFundingAndPayment(request.getFundingUid())
					.orElseThrow(() -> new IllegalArgumentException("펀딩 내역이 없습니다."));

			// 결제 상태 및 금액 검증
			validatePayment(iamportResponse, fund);

			// 결제 상태 변경
			fund.getPayment().changePaymentStatus(PaymentStatus.O, iamportResponse.getResponse().getImpUid());
			fund.setStatus(Status.A);
			fund.getMessage().setStatus(Status.A);

			// 모금 성공 -> 금액 증가
			Integer sumAmount = fund.getPayment().getAmount() + fund.getProduct().getCurrentAmount();
			fund.getProduct().setCurrentAmount(sumAmount);

			return iamportResponse;

		} catch (IamportResponseException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void validatePayment(IamportResponse<Payment> iamportResponse, Funding fund)
			throws IOException, IamportResponseException {

		// 모금 금액 초과인지?
		Integer sumAmount = fund.getPayment().getAmount() + fund.getProduct().getCurrentAmount();
	    // 총 모금액이 목표 금액을 초과하는 경우
	    if (sumAmount > fund.getProduct().getGoalAmount()) {
	        // 전액 결제 취소 및 예외 처리
	        cancelFullPayment(iamportResponse, fund);
	        throw new RuntimeException("펀딩 가능 금액 초과");
	    }
	      
		
		// !(결제 됬는지?) || 실 결제 금액 != db 저장된 금액 || 결제가능 금액 초과했는지
		if (!iamportResponse.getResponse().getStatus().equals("paid")
				|| iamportResponse.getResponse().getAmount().intValue() != fund.getPayment().getAmount()) {

			// 결제 검증 실패 시, 결제 취소 및 예외 처리
			cancelFullPayment(iamportResponse, fund);

			throw new RuntimeException("결제 미완료 또는 결제금액 위변조 의심");
		}
	}

	private void cancelFullPayment(IamportResponse<Payment> iamportResponse, Funding fund)
			throws IOException, IamportResponseException {
		// 전액 결제 취소
		iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true));

		// DB 업데이트 모금금액 변경, 결제 상태 변경
		Integer deductAmount = fund.getProduct().getCurrentAmount() - fund.getPayment().getAmount();
		fund.getProduct().setCurrentAmount(deductAmount);

		fund.getPayment().changePaymentStatus(PaymentStatus.C, "");
		fund.setStatus(Status.D);
		fund.getMessage().setStatus(Status.D);

		// 예외 발생
		throw new RuntimeException("결제 미완료 또는 결제금액 위변조 의심");
	}

	public boolean cancelPayment(String fundingUid, String socialId) {

		Funding funding = fundingRepository.findFundingAndPayment(fundingUid)
				.orElseThrow(() -> new IllegalArgumentException("펀딩 내역이 없습니다."));

		// 펀딩에 연결된 멤버의 소셜 로그인 ID 확인
		if (!funding.getMember().getSocialLoginId().equals(socialId)) {
			throw new RuntimeException("펀딩 UID와 멤버의 소셜 ID가 일치하지 않습니다.");
		}

		// db 에서 status 확인 결제됬는지
		if (funding.getStatus() == Status.D) {
			return false; // 이미 취소된 상태면 false 반환
		}

		// IAMPORT 결제 취소 API 호출
		try {
			IamportResponse<Payment> iamportResponse = iamportClient
					.paymentByImpUid(funding.getPayment().getPaymentUid());

			// 취소 정보, 취소 가능한 잔액 검증
			CancelData cancelData = new CancelData(iamportResponse.getResponse().getImpUid(), true);
			cancelData.setChecksum(new BigDecimal(funding.getPayment().getAmount()));

			// 전액 결제 취소
			IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(cancelData);

			// 결제 취소되면 코드 0으로 날라옴
			if (response.getCode() == 0 || response.getResponse().getStatus().equals("cancelled")) {
				// DB 업데이트 모금금액 변경, 결제 상태 변경
				Integer deductAmount = funding.getProduct().getCurrentAmount() - funding.getPayment().getAmount();
				funding.getProduct().setCurrentAmount(deductAmount);

				funding.getPayment().changePaymentStatus(PaymentStatus.C, "");
				funding.setStatus(Status.D);
				funding.getMessage().setStatus(Status.D);

				return true;
			}
		} catch (Exception e) {
			throw new RuntimeException("결제 취소 중 오류 발생", e);
		}

		return false;
	}

	@Transactional
	public void productCancel(String productLink, String socialId) {
		Product product = productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new DataNotFoundException("데이터가 없습니다."));

		if(product.getWishlist().getDeadline().toLocalDate().isBefore(LocalDate.now()))
			throw new WishListException(NOT_DEADLINE_AFTER);

		product.getFundingList().stream()
				.filter(funding -> funding.getStatus() == Status.A)
				.forEach(funding -> {
					String uid = funding.getFundingUid();
					cancelPayment(uid, socialId);
				});
	}

}