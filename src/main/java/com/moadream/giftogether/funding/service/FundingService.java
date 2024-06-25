package com.moadream.giftogether.funding.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.model.Payment;
import com.moadream.giftogether.funding.model.PaymentStatus;
import com.moadream.giftogether.funding.repository.FundingRepository;
import com.moadream.giftogether.funding.repository.PaymentRepository;
import com.moadream.giftogether.funding.repository.ProductRepository;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
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

    public Funding fund(String socialId,String productLink, Integer amount) {

    	Member member = findMemberBySocialId(socialId);
    	
        // 임시 결제내역 생성
        Payment payment = Payment.builder()
                .amount(amount) // 검증 할 값
                .status(PaymentStatus.R)
                .build();

        paymentRepository.save(payment);
        
//        log.info("member id = " + member.getId());
//        List<Product> list = productRepository.findAll();
//        for(Product p : list)
//        	log.info("log = " + p.getId());
        
        Product product = productRepository.findByProductLink(productLink)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with link: " + productLink));

        
        // 주문 생성
        Funding funding = Funding.builder()
                .amount(amount)
                .status(Status.A)
                .fundingUid(UUID.randomUUID().toString())
                .payment(payment)
                .member(member)
                .product(product)
                .build();

        return fundingRepository.save(funding);
    }
    
    private Member findMemberBySocialId(String socialId) {
        return memberRepository.findBySocialLoginId(socialId)
                .orElseThrow(() -> new RuntimeException("Member not found for socialId: " + socialId));
    }

}