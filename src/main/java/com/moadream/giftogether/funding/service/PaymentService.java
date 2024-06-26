package com.moadream.giftogether.funding.service;

import java.io.IOException;

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

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

	private final FundingRepository fundingRepository;
	private final PaymentRepository paymentRepository;
	private final IamportClient iamportClient;

	public RequestPayDto findRequestDto(String fundingUid) {

		Funding fund = fundingRepository.findFundingAndPaymentAndMember(fundingUid)
				.orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

		return RequestPayDto.builder()
				.buyerAddress(fund.getMember().getAddress()).paymentAmount(fund.getPayment().getAmount())
				.fundingUid(fund.getFundingUid()).build();
	}

	public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {

		try {
			// 결제 단건 조회(아임포트)
			IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
			// 주문내역 조회
			Funding fund = fundingRepository.findFundingAndPayment(request.getFundingUid())
					.orElseThrow(() -> new IllegalArgumentException("주문 내역이 없습니다."));

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

		//  결제 됬는지? || 실 결제 금액 != db 저장된 금액
		if (!iamportResponse.getResponse().getStatus().equals("paid") ||
				iamportResponse.getResponse().getAmount().intValue() != fund.getPayment().getAmount()) {

			// 주문, 결제 삭제
			fundingRepository.delete(fund);
			paymentRepository.delete(fund.getPayment());

			fund.getPayment().changePaymentStatus(PaymentStatus.C, "");
			fund.setStatus(Status.D);
			fund.getMessage().setStatus(Status.D);
			// 결제 취소
			iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true)); // ,new BigDecimal(iamportPrice)

			throw new RuntimeException("결제 미완료 또는 결제금액 위변조 의심");
		}
	}

}