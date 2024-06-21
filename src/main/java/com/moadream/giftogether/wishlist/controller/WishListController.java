package com.moadream.giftogether.wishlist.controller;


import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.service.WishListService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/wishlists")
public class WishListController {

    private final WishListService wishListService;

    public String WishListCreate(@Valid WishListForm wishListForm, HttpSession session){
        if(session == null)
            log.error("세션이 없습니다.");

        String socialId = session.getAttribute("kakaoId").toString();

        wishListService.createWishList(wishListForm, socialId);
        log.info("CONTROLLER = [" + socialId + "]" + "새 위시리스트 생성");

        return "redirect:/wishlists";
    }
}
