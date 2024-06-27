package com.moadream.giftogether.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public String resourceException(ResourceNotFoundException e, Model model, HttpServletRequest request) {
        log.error("{}", e.getMessage());

        model.addAttribute("message", e.getMessage());
        model.addAttribute("link", request.getHeader("Referer"));

        return "error";
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public String sessionNotFoundException(SessionNotFoundException e, Model model) {
        log.error("{}", e.getMessage());

        model.addAttribute("message", e.getMessage());
        model.addAttribute("link", e.getExceptionCode().getLink());


        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String exception(NoResourceFoundException e, Model model, HttpServletRequest request) {
        log.error("{}", e.getMessage());


        model.addAttribute("message", "주소가 잘못되었습니다.");
        model.addAttribute("link", request.getHeader("Referer"));

        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String globalException(Exception e, Model model, HttpServletRequest request) {
        log.error("{}", e.getMessage());


        model.addAttribute("message", e.getMessage());
        model.addAttribute("link", request.getHeader("Referer"));

        return "error";
    }
}
