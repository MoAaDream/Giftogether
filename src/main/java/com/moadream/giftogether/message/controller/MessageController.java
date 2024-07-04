package com.moadream.giftogether.message.controller;

import static com.moadream.giftogether.global.exception.GlobalExceptionCode.SESSION_NOT_FOUND;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.moadream.giftogether.global.exception.SessionNotFoundException;
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

	// 위시리스트에서 버튼 만들어야함 기능만 있음
	@GetMapping("/{wishlist_link}/messages")
	public String wishlistMessage(Model model, @PathVariable("wishlist_link") String wishlistLink,
			HttpSession session) {
		String socialId = checkSession(session);
		List<MessageFundDto> messageList = this.messageService.getMessageFunding(wishlistLink);
		int totalAmount = 0;
		int totalPeople = messageList.size();

		for (MessageFundDto message : messageList) {
			totalAmount += message.getAmount();
		}

		WishList wishlist = this.wishListRepository.findFirstByLink(wishlistLink).get();
		model.addAttribute("messageList", messageList);
		model.addAttribute("wishlistLink", wishlistLink);
		model.addAttribute("wishlist", wishlist);
		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("totalPeople", totalPeople);

		if (wishlist.getMember().getSocialLoginId().equals(socialId)) {
			return "message_wishlist";
		}
		return "product_list";
	}

	// 이건 안씀 제품 컨트롤에 메시지리스트(펀딩) 추가했음 지워도됨 일단 보류
	@GetMapping("/products/{product_link}/messages")
	public String productMessage(Model model, @PathVariable("product_link") String productLink, HttpSession session) {
		String socialId = checkSession(session);
		List<MessageFundDto> messageList = this.messageService.getMessageFundingProduct(productLink);
		Product product = this.productRepository.findByProductLink(productLink).get();
		int totalPeople = messageList.size();

		model.addAttribute("messageList", messageList);
		model.addAttribute("productLink", productLink);
		model.addAttribute("product", product);
		model.addAttribute("totalPeople", totalPeople);

		if (product.getWishlist().getMember().getSocialLoginId().equals(socialId)) {
			return "message_product";
		}
		return "product_detail";
	}

	// 메시지 가져오는거는 펀딩 가져올때 같이 가져옴
	@PostMapping("/message/{fundinguid}")
	public String messageModify(@PathVariable("fundinguid") String fundingUid, Model model,
			@RequestParam(name = "message") String message, RedirectAttributes redirectAttributes) {
		// funding/detail에서 유저 검증한 완료한 페이지임 필요한가?
		// String socialId = checkSession(session);
		// 메시지 수정
		messageService.modifyMessage(fundingUid, message);

		redirectAttributes.addFlashAttribute("successMessage", "메시지 수정이 완료되었습니다.");
		return "redirect:/fundings/detail/" + fundingUid;
	}

	private String checkSession(HttpSession session) {
		if (session == null)
			throw new SessionNotFoundException(SESSION_NOT_FOUND);

		if (session.getAttribute("kakaoId") == null)
			throw new SessionNotFoundException(SESSION_NOT_FOUND);
		return session.getAttribute("kakaoId").toString();
	}
}
