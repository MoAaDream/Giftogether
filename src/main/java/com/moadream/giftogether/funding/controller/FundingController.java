package com.moadream.giftogether.funding.controller;

import static com.moadream.giftogether.global.exception.GlobalExceptionCode.SESSION_NOT_FOUND;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.model.FundingDetailsDTO;
import com.moadream.giftogether.funding.model.FundingRequest;
import com.moadream.giftogether.funding.service.FundingQueueManager;
import com.moadream.giftogether.funding.service.FundingService;
import com.moadream.giftogether.global.exception.SessionNotFoundException;
import com.moadream.giftogether.member.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/fundings")
public class FundingController {

	private final FundingService fundingService;
	private final MemberService memberService;
	private final FundingQueueManager queueManager;

	@GetMapping("/{productlink}")
	public String fund(@PathVariable(name = "productlink", required = true) String productLink,
			@RequestParam(name = "fundingUid", required = false) String fundingUid, HttpSession session, Model model) {

		int[] amountOptions = fundingService.getFundingAmounts(productLink);

		String productName = fundingService.getProductName(productLink);
		String productDescription = fundingService.getProductDescription(productLink);
		String productProductImg = fundingService.getProductProductImg(productLink);

		model.addAttribute("amountOptions", amountOptions);
		model.addAttribute("fundingUid", fundingUid);
		model.addAttribute("productLink", productLink);
		model.addAttribute("productName", productName);
		model.addAttribute("productDescription", productDescription);
		model.addAttribute("productProductImg", productProductImg);

		return "funding/order";
	}

	@PostMapping("/{productlink}")
	public String setAmount(@PathVariable(name = "productlink", required = true) String productLink,
			@RequestParam(name = "message") String messageF, @RequestParam("amount") Integer amount,
			HttpSession session, RedirectAttributes redirectAttributes) {

		// 결제 이전 금액 설정 단계에서 금액 제한
		try {
			fundingService.validateAmount(amount, productLink);

		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/fundings/" + productLink;
		}

		String socialId = checkSession(session);
		FundingRequest request = new FundingRequest(socialId, productLink, amount, messageF);

		CompletableFuture<FundingRequest> future = queueManager.addFundingRequest(request);
		return future.thenApply(r -> {
			// 처리가 완료된 후의 로직
			redirectAttributes.addFlashAttribute("message", "주문이 접수되었습니다. 처리 중입니다.");
			return "redirect:/fundings/payment/" + r.getFundingUid();
		}).exceptionally(e -> {
			// 예외 처리
			redirectAttributes.addFlashAttribute("errorMessage", "주문 처리 중 오류가 발생했습니다.");
			return "redirect:/fundings/" + productLink;
		}).join(); // CompletableFuture가 완료될 때까지 기다립니다.
	}

	// 테스트 큐매니저 있음
	@PostMapping("/yes/{productlink}")
	public ResponseEntity<Object> setAmountY(@PathVariable(name = "productlink", required = true) String productLink,
			@RequestParam(name = "message") String messageF, @RequestParam("amount") Integer amount,
			HttpSession session, RedirectAttributes redirectAttributes) {
 

		String socialId = checkSession(session);
		FundingRequest request = new FundingRequest(socialId, productLink, amount, messageF);

		CompletableFuture<FundingRequest> future = queueManager.addFundingRequest(request);
        try {
            FundingRequest result = future.join(); // CompletableFuture가 완료될 때까지 기다립니다. 
            return ResponseEntity.ok("redirect:/fundings/payment/" + result.getFundingUid());
        } catch (Exception e) { 
            return ResponseEntity.status(500).body("Error processing your funding request.");
        }
	}

	// 테스트2 큐매니저 없음
	@PostMapping("/no/{productlink}")
	public ResponseEntity<Object> setAmountN(@PathVariable(name = "productlink", required = true) String productLink,
			@RequestParam(name = "message") String messageF, @RequestParam("amount") Integer amount,
			HttpSession session, RedirectAttributes redirectAttributes) {
 

		String socialId = checkSession(session);
		Funding funding = fundingService.fund(socialId, productLink, amount, messageF);

		if (funding == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("주문 실패");
		}

		return ResponseEntity.ok("주문 성공");
	}

	@GetMapping("/detail/{fundingUid}")
	public String getFundingDetail(@PathVariable(name = "fundingUid", required = true) String fundingUid, Model model,
			HttpSession session, RedirectAttributes redirectAttributes) {

		String socialId = checkSession(session);

		String productLink = fundingService.getProductLinkByFundingUid(fundingUid);
		model.addAttribute("productLink", productLink);

		FundingDetailsDTO fundingDetail = fundingService.getFundingDetailByUid(fundingUid, socialId);
		if (!fundingDetail.isCanViewDetails()) {
			redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to view this page.");
			return "redirect:/home";
		}

		model.addAttribute("fundingDetail", fundingDetail);
		if (model.containsAttribute("successMessage")) {
			model.addAttribute("message", model.getAttribute("successMessage"));
		}
		if (model.containsAttribute("errorMessage")) {
			model.addAttribute("message", model.getAttribute("errorMessage"));
		}

		return "funding/detail";
	}

	private String checkSession(HttpSession session) {
		if (session == null)
			throw new SessionNotFoundException(SESSION_NOT_FOUND);

		if (session.getAttribute("kakaoId") == null)
			throw new SessionNotFoundException(SESSION_NOT_FOUND);
		return session.getAttribute("kakaoId").toString();
	}
}
