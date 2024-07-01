package com.moadream.giftogether.funding.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.moadream.giftogether.global.exception.SessionNotFoundException;
import com.moadream.giftogether.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.model.FundingDetailsDTO;
import com.moadream.giftogether.funding.service.FundingService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.moadream.giftogether.global.exception.GlobalExceptionCode.SESSION_NOT_FOUND;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/fundings")
public class FundingController {

	private final FundingService fundingService;
	private final MemberService memberService;

	@GetMapping("/{productlink}")
	public String fund(@PathVariable(name = "productlink", required = true) String productLink,
			@RequestParam(name = "messageT", required = false) String messageT,
			@RequestParam(name = "fundingUid", required = false) String id, HttpSession session, Model model) {
		String socialId = checkSession(session);
		memberService.checkBlackList(socialId);

		int[] amountOptions = fundingService.getFundingAmounts(productLink);
		String productName = fundingService.getProductName(productLink);
		model.addAttribute("amountOptions", amountOptions);
		model.addAttribute("messageT", messageT);
		model.addAttribute("fundingUid", id);
		model.addAttribute("productName", productName);


		// 제품의 펀딩 리스트
		List<FundingDetailsDTO> fundingDetailP = fundingService.findFundingsByProductLink(productLink);
		model.addAttribute("fundingDetailP", fundingDetailP);


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
		Funding funding = fundingService.fund(socialId, productLink, amount, messageF);

		String messageT = "주문 실패 ";
		if (funding != null) {
			messageT = "주문 성공 " + amount + "<br>메시지 : " + messageF;
		}
		String encode = URLEncoder.encode(messageT, StandardCharsets.UTF_8);

		return "redirect:/fundings/" + productLink + "?messageT=" + encode + "&fundingUid=" + funding.getFundingUid();
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

	@GetMapping("/my")
	public String getMyPayments(HttpSession session, Model model){
		// 나의 펀딩 리스트
		String socialId = checkSession(session);
		List<FundingDetailsDTO> fundingDetailM = fundingService.findFundingsBySocialId(socialId);
		model.addAttribute("fundingDetailM", fundingDetailM);


		return "member/pay_statics";
	}


	private String checkSession(HttpSession session) {
		if (session == null)
			throw new SessionNotFoundException(SESSION_NOT_FOUND);

		if (session.getAttribute("kakaoId") == null)
			throw new SessionNotFoundException(SESSION_NOT_FOUND);

		return session.getAttribute("kakaoId").toString();
	}
}
