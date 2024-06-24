package com.moadream.giftogether.funding;

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

import com.moadream.giftogether.funding.model.PaymentCallbackRequest;
import com.moadream.giftogether.funding.model.RequestPayDto;
import com.moadream.giftogether.funding.service.PaymentService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/fundings")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/payment/{id}") // id = 펀딩의 fundingUid (UUid)
    public String paymentPage(@PathVariable(name = "id", required = false) String id,
                              Model model) {

        RequestPayDto requestDto = paymentService.findRequestDto(id);
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
}