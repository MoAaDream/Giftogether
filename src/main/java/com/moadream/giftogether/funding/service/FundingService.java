package com.moadream.giftogether.funding.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.FundingRepository;
import com.moadream.giftogether.funding.PaymentRepository;
import com.moadream.giftogether.funding.PaymentStatus;
import com.moadream.giftogether.funding.ProductRepository;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.model.Payment;
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

    public Funding fund(Member member,Long productId) {

        // 임시 결제내역 생성
        Payment payment = Payment.builder()
                .amount(1000) // 검증 
                .status(PaymentStatus.R)
                .build();

        paymentRepository.save(payment);
        
//        log.info("member id = " + member.getId());
//        List<Product> list = productRepository.findAll();
//        for(Product p : list)
//        	log.info("log = " + p.getId());
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        
        // 주문 생성
        Funding funding = Funding.builder()
                .amount(1000)
                .status(Status.A)
                .fundingUid(UUID.randomUUID().toString())
                .payment(payment)
                .member(member)
                .product(product)
                .build();

        return fundingRepository.save(funding);
    }
}