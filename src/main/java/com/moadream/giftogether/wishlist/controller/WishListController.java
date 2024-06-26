package com.moadream.giftogether.wishlist.controller;


import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishlistDto;
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
        return "wishlists/wishlist_form";
    }

    @PostMapping("/")
    public String wishListCreate(@Valid @ModelAttribute WishListForm wishListForm, HttpSession session, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("wishListForm", wishListForm);
            return "wishlists/wishlist_form";
        }

        String socialId = checkSession(session);
      
        wishListService.createWishList(wishListForm, socialId);
        log.info("CONTROLLER = [" + socialId + "]" + "새 위시리스트 생성");

        // 알림 메시지를 모델에 추가합니다.
        model.addAttribute("message", "제출이 완료되었습니다!");
        model.addAttribute("link", "/wishlists/my/0");

        return "wishlists/wishlistalert";
    }

    @GetMapping("/{wishlistlink}")
    public String getModifyForm(@PathVariable("wishlistlink") String wishlistLink, @Valid @ModelAttribute WishListForm wishListForm,
                                BindingResult bindingResult, HttpSession session, Model model){
        String socialId = checkSession(session);

        if(!wishListService.checkMyPage(socialId, wishlistLink)) {
            model.addAttribute("message", "본인이 아닙니다.");
            model.addAttribute("link",  "/products/" + wishlistLink);
            return "wishlists/wishlistalert";
        }

        WishListForm wishlist = wishListService.getWishlist(wishlistLink);

        model.addAttribute("wishListForm", wishlist);
        return "wishlists/wishlist_modifyform";
    }

    @PostMapping("/{wishlistlink}")
    public String wishListModify(@PathVariable("wishlistlink") String wishlistLink, @Valid @ModelAttribute WishListForm wishListForm,
                                 BindingResult bindingResult, HttpSession session, Model model) {
        String socialId = checkSession(session);

        wishListService.modifyWishList(wishListForm, socialId, wishlistLink);
        log.info("CONTROLLER = [" + socialId + "]" + "위시리스트 수정");

        model.addAttribute("message", "제출이 완료되었습니다!");
        model.addAttribute("link", "/products/" + wishlistLink);


        return "wishlists/wishlistalert";
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

        Page<WishlistDto> list = wishListService.getList(socialId, page);
        model.addAttribute("paging", list);

        return "wishlists/wishlists";
    }

    private String checkSession(HttpSession session){
        if (session == null)
            log.error("세션이 없습니다.");

        return session.getAttribute("kakaoId").toString();

    }
}
