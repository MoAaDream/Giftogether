package com.moadream.giftogether.message.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.moadream.giftogether.message.model.MessageFundDto;
import com.moadream.giftogether.message.service.MessageService;
import com.moadream.giftogether.product.Repository.ProductRepository;
import com.moadream.giftogether.product.model.Product;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.repository.WishListRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
public class MessageController {
	
	private final MessageService messageService;
	private final WishListRepository wishListRepository;
	private final ProductRepository productRepository;

	
	@GetMapping("/{wishlist_link}/messages")
    public String wishlistMessage(Model model,  @PathVariable("wishlist_link") String wishlistLink, HttpSession session) {
		String socialId = checkSession(session);
		List<MessageFundDto> messageList = this.messageService.getMessageFunding(wishlistLink);
		int totalAmount = 0;
		int totalPeople = messageList.size();

		for (MessageFundDto message : messageList) {
			totalAmount += message.getAmount();
		}
		
		WishList wishlist = this.wishListRepository.findFirstByLink(wishlistLink).get();
        model.addAttribute("messageList", messageList);
        model.addAttribute("wishlistLink",wishlistLink);
        model.addAttribute("wishlist", wishlist); 
        model.addAttribute("totalAmount", totalAmount); 
        model.addAttribute("totalPeople", totalPeople);
		
		if (wishlist.getMember().getSocialLoginId().equals(socialId)){
			return "message_wishlist";
		}
		return "product_list";
    }
	
	private String checkSession(HttpSession session){
        if (session == null)
            log.error("세션이 없습니다.");

        return session.getAttribute("kakaoId").toString();

    }
	
	@GetMapping("/products/{product_link}/messages")
    public String productMessage(Model model,  @PathVariable("product_link") String productLink, HttpSession session) {
		String socialId = checkSession(session);
		List<MessageFundDto> messageList = this.messageService.getMessageFundingProduct(productLink);
		Product product = this.productRepository.findByProductLink(productLink).get();
		int totalPeople = messageList.size();

		
        model.addAttribute("messageList", messageList);
        model.addAttribute("productLink",productLink);
        model.addAttribute("product", product); 
        model.addAttribute("totalPeople", totalPeople);
		
		if (product.getWishlist().getMember().getSocialLoginId().equals(socialId)){
			return "message_product";
		}
		return "product_detail";
    }


}
