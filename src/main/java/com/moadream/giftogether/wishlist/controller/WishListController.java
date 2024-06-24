package com.moadream.giftogether.wishlist.controller;


import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishListModifyForm;
import com.moadream.giftogether.wishlist.service.WishListServiceI;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/wishlists")
public class WishListController {

    private final WishListServiceI wishListService;

    public String wishListCreate(@Valid WishListForm wishListForm, HttpSession session) {
        String socialId = checkSession(session);


        wishListService.createWishList(wishListForm, socialId);
        log.info("CONTROLLER = [" + socialId + "]" + "새 위시리스트 생성");

        return "redirect:/wishlists";
    }

    @PostMapping("/{wishlistlink}")
    public String wishListModify(@PathVariable("wishlistlink") String wishlistLink, @Valid WishListModifyForm wishListModifyForm, HttpSession session) {
        String socialId = checkSession(session);

        wishListService.modifyWishList(wishListModifyForm, socialId, wishlistLink);
        log.info("CONTROLLER = [" + socialId + "]" + "위시리스트 수정");
        
        return "redirect:/wishlists";
    }


    private String checkSession(HttpSession session){
        if (session == null)
            log.error("세션이 없습니다.");

        return session.getAttribute("kakaoId").toString();

    }
}
