package com.moadream.giftogether.funding;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.service.FundingService;
import com.moadream.giftogether.funding.service.MemberService;
import com.moadream.giftogether.member.model.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/fundings")
public class FundingController {

    private final MemberService memberService;
    private final FundingService fundingService;

     
    
    @GetMapping("/{productId}")  
    public String fund(@PathVariable(name = "productId", required = true) Long productId,
    					@RequestParam(name = "message", required = false) String message,
                        @RequestParam(name = "fundingUid", required = false) String id,
                        Model model) {

        model.addAttribute("message", message);
        model.addAttribute("fundingUid", id);

        return "funding/order";
    }
    

    @PostMapping("/{productId}")
    public String autoOrder(@PathVariable(name = "productId", required = true) Long productId) {
    	Member member = memberService.findById(1L);// TODO 진짜 멤버 필요
        Funding funding = fundingService.fund(member, productId);
//        Long productId = funding.getProduct().getId();

        String message = "주문 실패";
        if(funding != null) {
            message = "주문 성공";
        }

        String encode = URLEncoder.encode(message, StandardCharsets.UTF_8);

        return "redirect:/fundings/"+ productId +"?message="+encode+"&fundingUid="+funding.getFundingUid();
    }
}



