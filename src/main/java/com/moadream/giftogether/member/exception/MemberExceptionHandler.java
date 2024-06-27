package com.moadream.giftogether.member.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class MemberExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public String memberException(MemberException exception, Model model, HttpServletRequest request) {
        log.error("{}", exception.getMessage());

        model.addAttribute("message", exception.getMessage());
        model.addAttribute("link", exception.getExceptionCode().getLink());


        return "error";
    }
}
