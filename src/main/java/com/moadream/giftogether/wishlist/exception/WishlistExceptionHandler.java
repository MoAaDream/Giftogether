package com.moadream.giftogether.wishlist.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class WishlistExceptionHandler {

    @ExceptionHandler(WishListException.class)
    public String wishlistException(WishListException exception, Model model, HttpServletRequest request) {
        log.error("{}", exception.getMessage());


        model.addAttribute("message", exception.getMessage());
        model.addAttribute("link", request.getHeader("Referer"));


        return "error";
    }
}
