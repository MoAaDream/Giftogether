package com.moadream.giftogether.funding.controller;

import static com.moadream.giftogether.global.exception.GlobalExceptionCode.SESSION_NOT_FOUND;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
			@RequestParam(name = "messageT", required = false) String messageT,
			@RequestParam(name = "fundingUid", required = false) String fundingUid, HttpSession session, Model model) {

		int[] amountOptions = fundingService.getFundingAmounts(productLink);
		
		String productName = fundingService.getProductName(productLink);
		model.addAttribute("amountOptions", amountOptions);
		model.addAttribute("messageT", messageT);
		model.addAttribute("fundingUid", fundingUid); 
		model.addAttribute("productLink", productLink); 
		model.addAttribute("productName", productName);
		
		 
 
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
//		Funding funding = fundingService.fund(socialId, productLink, amount, messageF);
// 
//		
//		String messageT = "주문 실패 ";
//		if (funding != null) {
//			messageT = "주문 성공 " + amount + "<br>메시지 : " + messageF;
//		}
//		String encode = URLEncoder.encode(messageT, StandardCharsets.UTF_8);
//
//		return "redirect:/fundings/payment/" + funding.getFundingUid();
		
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
        }).join();  // CompletableFuture가 완료될 때까지 기다립니다.
        
//        queueManager.addFundingRequest(request);
//        redirectAttributes.addFlashAttribute("message", "주문이 접수되었습니다. 처리 중입니다.");
//        log.info("ㅁㅁㅁ제발 " +request.getFundingUid());
//        return "redirect:/fundings/payment/" + request.getFundingUid(); // 임의 UID 할당 로직 필요
	}

	@GetMapping("/detail/{fundinguid}")
	public String getFundingDetail(@PathVariable(name = "fundinguid", required = true) String fundingUid, Model model,
			RedirectAttributes redirectAttributes) {

		FundingDetailsDTO fundingDetail = fundingService.getFundingDetailByUid(fundingUid);

		model.addAttribute("fundingDetail", fundingDetail);
		// model에서 flash attribute를 확인하고 추가 처리
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
