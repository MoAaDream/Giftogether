package com.moadream.giftogether.funding.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.moadream.giftogether.funding.model.PaymentCallbackRequest;
import com.moadream.giftogether.funding.model.RequestPayDto;
import com.moadream.giftogether.funding.service.PaymentService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/fundings")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/payment/{fundinguid}") // fundingUid (UUid)
    public String paymentPage(@PathVariable(name = "fundinguid") String fundingUid,
                              Model model) {

        RequestPayDto requestDto = paymentService.findRequestDto(fundingUid);
        model.addAttribute("requestDto", requestDto);
        return "funding/payment";
    }

    @ResponseBody
    @PostMapping("/payment")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentCallbackRequest request) {
        IamportResponse<Payment> iamportResponse = paymentService.paymentByCallback(request);

        log.info("결제 응답={}", iamportResponse.getResponse().toString());

        return new ResponseEntity<>(iamportResponse, HttpStatus.OK);
    }

    @GetMapping("/success-payment")
    public String successPaymentPage() {
        return "funding/success-payment";
    }

    @GetMapping("/fail-payment")
    public String failPaymentPage() {
        return "funding/fail-payment";
    }
    
    
    @PostMapping("/cancel-payment/{id}")
    public String cancelPayment(@PathVariable("id") String fundingUid,HttpSession session, RedirectAttributes redirectAttributes) {
        try {
        	

    	String socialId = checkSession(session);
          boolean result = paymentService.cancelPayment(fundingUid,socialId);
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", "결제가 성공적으로 취소되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "이미 취소됬습니다."); 
            }
        } catch (Exception e) {
            log.error("결제 취소 오류", e);
            redirectAttributes.addFlashAttribute("errorMessage", "시스템 오류로 인한 결제 취소 실패.");
        }
        return "redirect:/fundings/detail/" + fundingUid ;  
    }

	private String checkSession(HttpSession session) {
		if (session == null)
			log.error("세션이 없습니다.");
		return session.getAttribute("kakaoId").toString();
	}
}