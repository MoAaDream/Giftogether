package com.moadream.giftogether.funding.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.model.FundingDetailsDTO;
import com.moadream.giftogether.funding.service.FundingService;
import com.moadream.giftogether.member.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/fundings")
@SessionAttributes("confirmedAmount") // 세션에 confirmedAmount 속성을 저장
public class FundingController {

	private final MemberService memberService;
	private final FundingService fundingService;

	@GetMapping("/{productlink}")
	public String fund(@PathVariable(name = "productlink", required = true) String productLink,
			@RequestParam(name = "messageT", required = false) String messageT,
			@RequestParam(name = "fundingUid", required = false) String id, HttpSession session, Model model) {

		int[] amountOptions = { 5000, 10000, 20000, 30000, 40000, 50000 };
		model.addAttribute("amountOptions", amountOptions);
		model.addAttribute("messageT", messageT);
		model.addAttribute("fundingUid", id);

		// push 전에 제거 테스트용
		session.setAttribute("kakaoId", "3051424432");

		// 제품의 펀딩 리스트
		List<FundingDetailsDTO> fundingDetailP = fundingService.findFundingsByProductLink(productLink);
		model.addAttribute("fundingDetailP", fundingDetailP);

		// 나의 펀딩 리스트
		String socialId = checkSession(session);
		List<FundingDetailsDTO> fundingDetailM = fundingService.findFundingsBySocialId(socialId);
		model.addAttribute("fundingDetailM", fundingDetailM);

		return "funding/order";
	}

	@PostMapping("/{productlink}")
	public String setAmount(@PathVariable(name = "productlink", required = true) String productlink,
			@RequestParam(name = "message") String messageF, @RequestParam("amount") Integer amount,
			HttpSession session) {
		// 사용자가 선택한 금액을 세션에 저장
		session.setAttribute("confirmedAmount", amount);
		
		String socialId = checkSession(session);
		Funding funding = fundingService.fund(socialId, productlink, amount, messageF);


		String messageT = "주문 실패 ";
		if (funding != null) {
			messageT = "주문 성공 " + amount + "<br>메시지 : " + messageF;
		}
		String encode = URLEncoder.encode(messageT, StandardCharsets.UTF_8);

		return "redirect:/fundings/" + productlink + "?messageT=" + encode + "&fundingUid=" + funding.getFundingUid();
	}

	@GetMapping("/detail/{fundinguid}")
	public String getFundingDetail(@PathVariable(name = "fundinguid", required = true) String fundingUid, Model model, RedirectAttributes redirectAttributes) {

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
			log.error("세션이 없습니다.");
		return session.getAttribute("kakaoId").toString();
	}
}
