package com.moadream.giftogether.bank.service;

import static com.moadream.giftogether.member.exception.MemberExceptionCode.NOT_FOUND_SOCIAL_ID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.bank.model.Bank;
import com.moadream.giftogether.bank.model.BankForm;
import com.moadream.giftogether.bank.repository.BankRepository;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.exception.MemberException;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.product.Repository.ProductRepository;
import com.moadream.giftogether.product.model.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class BankService {

	private final ProductRepository productRepository;
	private final BankRepository bankRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public boolean refund(BankForm bankForm, String productLink, String socialId) {

		Member member = findMemberBySocialId(socialId);
	
	
		Product product = productRepository.findByProductLink(productLink)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with link: " + productLink));

        // 회원 ID 검증 및 제품 금액 검증
        if (member.getId() != product.getWishlist().getMember().getId() || product.getCurrentAmount() == 0) {
            throw new IllegalStateException("Refund cannot be processed due to invalid conditions.");
        }	
		
		Bank bank = Bank.createBank(bankForm, member, product.getCurrentAmount() ,productLink);
 		bankRepository.save(bank);
 		

		product.setCurrentAmount(0);
		product.setStatus(Status.D);
		productRepository.save(product);
		
		return true;
	}

	private Member findMemberBySocialId(String socialId) {
		return memberRepository.findBySocialLoginId(socialId)
				.orElseThrow(() -> new MemberException(NOT_FOUND_SOCIAL_ID));
	}
}
