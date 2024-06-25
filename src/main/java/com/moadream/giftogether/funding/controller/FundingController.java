package com.moadream.giftogether.funding.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.moadream.giftogether.funding.model.Funding;
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
    public String fund(@PathVariable(name = "productlink", required = true) String productlink,
    					@RequestParam(name = "message", required = false) String message,
                        @RequestParam(name = "fundingUid", required = false) String id,
                        HttpSession session,
                        Model model) {

//		// 세션에서 사용자 이름 가져오기
//		String nickname = (String) session.getAttribute("nickname");
//		model.addAttribute("name", nickname);
    	 
        int[] amountOptions = {5000, 10000, 20000, 30000, 40000, 50000};
        model.addAttribute("amountOptions", amountOptions);
        model.addAttribute("message", message);
        model.addAttribute("fundingUid", id);
        
          

        return "funding/order";
    }
    

    @PostMapping("/{productlink}")
    public String setAmount(@PathVariable(name = "productlink", required = true) String productlink,@RequestParam("amount") Integer amount, HttpSession session) {
    	// 사용자가 선택한 금액을 세션에 저장
    	session.setAttribute("confirmedAmount", amount); // 사용자가 선택한 금액을 세션에 저장
		// 세션에서 사용자 이름 가져오기 
    	String socialId = checkSession(session);
        Funding funding = fundingService.fund(socialId, productlink,amount);
//        Long productId = funding.getProduct().getId();

        String message = "주문 실패";
        if(funding != null) {
            message = "주문 성공";
        }

        String encode = URLEncoder.encode(message, StandardCharsets.UTF_8);

        return "redirect:/fundings/"+ productlink +"?message="+encode+"&fundingUid="+funding.getFundingUid();
    }
    

    private String checkSession(HttpSession session){
        if (session == null)
            log.error("세션이 없습니다.");
        return session.getAttribute("kakaoId").toString();
    }
}



