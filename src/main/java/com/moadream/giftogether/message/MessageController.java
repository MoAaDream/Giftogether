package com.moadream.giftogether.message;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.product.Service.ProductService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
@RequestMapping("/message")
public class MessageController {

    private final ProductService productService;
    private final MessageService messageService;
    //private final WishlistService wishlistService;

    @GetMapping("/list")
    public String list(Model model) {
		List<Message> messageList = this.messageService.getList();
        model.addAttribute("messageList", messageList);
		return "message_list";
    }
    /*
    @PostMapping("/create/{id}")
    public String createMessage(Model model, @PathVariable("id") Long id, 
    		@RequestParam(value="amount") int amount, @RequestParam(defaultValue = "축하해요!", value="content") String content) {
        Product product = this.productService.getProduct(id);
        WishList wishlist = this.wishlistService.getWishlist(product.getWishlist().getId());
        this.messageService.create(wishlist,amount, content);
        return String.format("redirect:/product/detail/%s", id);
    }*/
}
