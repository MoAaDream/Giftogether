package com.moadream.giftogether.wishlist.controller;


import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishListModifyForm;
import com.moadream.giftogether.wishlist.service.WishListServiceI;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/wishlists")
public class WishListController {

    private final WishListServiceI wishListService;


    @GetMapping("/")
    public String getMapping(Model model){
        model.addAttribute("wishListForm", new WishListForm());
        return "upload";
    }

    @PostMapping("/")
    public String wishListCreate(@Valid @ModelAttribute WishListForm wishListForm, HttpSession session, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("wishListForm", wishListForm);
            return "upload";
        }

        String socialId = checkSession(session);
      
        wishListService.createWishList(wishListForm, socialId);
        log.info("CONTROLLER = [" + socialId + "]" + "새 위시리스트 생성");

        return "redirect:/wishlists/my/0";
    }

    @PostMapping("/{wishlistlink}")
    public String wishListModify(@PathVariable("wishlistlink") String wishlistLink, @Valid WishListModifyForm wishListModifyForm, HttpSession session) {
        String socialId = checkSession(session);

        wishListService.modifyWishList(wishListModifyForm, socialId, wishlistLink);
        log.info("CONTROLLER = [" + socialId + "]" + "위시리스트 수정");
        
        return "redirect:/wishlists";
    }

    @DeleteMapping("/{wishlistlink}")
    public String wishlistDelete(@PathVariable("wishlistlink") String wishlistLink, HttpSession session){
        String socialId = checkSession(session);
        
        wishListService.deleteWishList(socialId, wishlistLink);
        log.info("CONTROLLER = [" + socialId + "]" + "위시리스트 삭제");

        return "redirect:/wishlists";
    }

    @GetMapping("/my/{page}")
    public String getAllWishlist(@PathVariable("page") int page, HttpSession session, Model model){
        String socialId = checkSession(session);

        Page<WishList> list = wishListService.getList(socialId, page);
        model.addAttribute("paging", list);

        return "wishlists";
    }

    private String checkSession(HttpSession session){
        if (session == null)
            log.error("세션이 없습니다.");

        return session.getAttribute("kakaoId").toString();

    }
}
