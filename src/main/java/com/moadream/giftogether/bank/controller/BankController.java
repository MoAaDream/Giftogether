package com.moadream.giftogether.bank.controller;

import static com.moadream.giftogether.global.exception.GlobalExceptionCode.SESSION_NOT_FOUND;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.moadream.giftogether.bank.model.BankForm;
import com.moadream.giftogether.bank.service.BankService;
import com.moadream.giftogether.global.exception.SessionNotFoundException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/bank")
public class BankController {

	private final BankService bankService;
 

	
	@PostMapping("/{productLink}")
	public ResponseEntity<?> processRefund(@ModelAttribute BankForm bankForm,
	                                       @PathVariable("productLink") String productLink,
	                                       HttpSession session, RedirectAttributes redirectAttributes) {
	    try {
	        String socialId = checkSession(session);
	        bankService.refund(bankForm, productLink, socialId);
	        return ResponseEntity.ok().body("환불 완료!");
	    } catch (IllegalStateException e) {
	        return ResponseEntity.badRequest().body("환불 실패! " + e.getMessage());
	    }
	}
	
    private String checkSession(HttpSession session) {
        if (session == null)
            throw new SessionNotFoundException(SESSION_NOT_FOUND);

        if (session.getAttribute("kakaoId") == null)
            throw new SessionNotFoundException(SESSION_NOT_FOUND);

        return session.getAttribute("kakaoId").toString();
    }
}