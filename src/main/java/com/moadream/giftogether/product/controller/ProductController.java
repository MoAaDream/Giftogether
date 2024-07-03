package com.moadream.giftogether.product.controller;


import static com.moadream.giftogether.global.exception.GlobalExceptionCode.SESSION_NOT_FOUND;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import com.moadream.giftogether.bank.model.BankForm;
import com.moadream.giftogether.funding.model.FundingDetailsDTO;
import com.moadream.giftogether.funding.service.FundingService;
import com.moadream.giftogether.global.exception.SessionNotFoundException;
import com.moadream.giftogether.member.service.MemberService;
import com.moadream.giftogether.product.Service.ProductService;
import com.moadream.giftogether.product.model.Product;
import com.moadream.giftogether.product.model.ProductForm;
import com.moadream.giftogether.product.model.ProductModifyForm;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.repository.WishListRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
//@RequestMapping("/products")
public class ProductController {


	private final FundingService fundingService;
	private final ProductService productService;
	private final MemberService memberService;
	private final WishListRepository wishListRepository;
	
	@GetMapping("/{wishlist_link}/products")
    public String list(Model model,  @PathVariable("wishlist_link") String wishlistLink, HttpSession session) {
		String socialId = checkSession(session);
		List<Product> productList = this.productService.getProductList(wishlistLink);
		WishList wishlist = this.wishListRepository.findFirstByLink(wishlistLink).get();
        model.addAttribute("productList", productList);
        model.addAttribute("wishlistLink",wishlistLink);
        model.addAttribute("wishlist", wishlist); 
		
		if (wishlist.getMember().getSocialLoginId().equals(socialId)){
			return "products/product_mylist";
		}
		return "products/product_list";
    }
	
	@GetMapping("/products/{product_link}")
	public String detail(HttpSession session, Model model, @PathVariable("product_link") String productLink) {
	    Product product = this.productService.getProduct(productLink);
	    model.addAttribute("product", product);
	    
		model.addAttribute("bankForm", new BankForm());
		
		int insufficientAmount = fundingService.getInsufficientAmount(productLink);
		model.addAttribute("insufficientAmount", insufficientAmount);

		
		// 날짜 지난 펀딩 모금 실패시 true 반환
	    boolean isDeadFundingComplete = productService.isDeadFundingComplete(productLink);
	    model.addAttribute("isDeadFundingComplete", isDeadFundingComplete);

	    // productLink의 유저가 지금 유저와 같으면 true
		String socialId = checkSession(session); 		
	    boolean isUserProduct = productService.isUserProduct(socialId, productLink);
	    model.addAttribute("isUserProduct", isUserProduct);
		// 제품의 펀딩 리스트
		List<FundingDetailsDTO> fundingDetailP = fundingService.findFundingsByProductLink(socialId, productLink);
		model.addAttribute("fundingDetailP", fundingDetailP);
		
//        for (FundingDetailsDTO s : fundingDetailP) {
//            log.info("ㅁㅁㅁ", s.isCanViewDetails());
//        }
	    return "products/product_detail";
	}
	
	@GetMapping("/{wishlist_link}/create")
	public String createProduct(Model model, @PathVariable("wishlist_link") String wishlistLink,  HttpSession session) {
		String socialId = checkSession(session);
		memberService.checkBlackList(socialId);
		WishList wishlist = this.wishListRepository.findFirstByLink(wishlistLink).get();
		model.addAttribute("productForm",new ProductForm());
		model.addAttribute("wishlistLink",wishlistLink);
		if (!wishlist.getMember().getSocialLoginId().equals(socialId)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "생성 권한이 없습니다.");
		}
		return "products/product_form";
	}

	
	@PostMapping("/{wishlist_link}/create")
	public String createProduct(Model model, @PathVariable("wishlist_link") String wishlistLink,
			@Valid @ModelAttribute ProductForm productForm, BindingResult bindingResult,  HttpSession session) {
		String socialId = checkSession(session);
		memberService.checkBlackList(socialId);
		WishList wishlist = this.wishListRepository.findFirstByLink(wishlistLink).get();
		String productLink = UUID.randomUUID().toString().replace("-", "");
		model.addAttribute("wishlistLink",wishlistLink);
		
		if (!wishlist.getMember().getSocialLoginId().equals(socialId)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "생성 권한이 없습니다.");
		}
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("productForm", productForm);
			return "products/product_form";
		}
		
		log.info("Creating product link: " + productForm.getUploadedImage());
		this.productService.create(productForm.getName(), productForm.getDescription(),productForm.getExternalLink(), 
				productForm.getUploadedImage(), productForm.getOptionDetail(), productForm.getGoalAmount(), productLink, wishlistLink);
		
	    return "redirect:/{wishlist_link}/products"; 
	}
	
	
	@GetMapping("/products/{product_link}/modify")
	public String modifyProduct(ProductModifyForm productModifyForm, Model model,
			@PathVariable("product_link") String productLink, HttpSession session) {
		Product product = this.productService.getProduct(productLink);
		String socialId = checkSession(session);
		model.addAttribute("product", product);
		
		if (!product.getWishlist().getMember().getSocialLoginId().equals(socialId)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		productModifyForm.setName(product.getName());
		productModifyForm.setDescription(product.getDescription());
		productModifyForm.setUploadedImage(product.getProductImg());
		productModifyForm.setOptionDetail(product.getOptionDetail());
		model.addAttribute("productModifyForm", product);
		
		return "products/product_modify";
	}
	
	
	@PostMapping("/products/{product_link}/modify")
	public String modifyProduct(Model model, @PathVariable("product_link") String productLink,
			@Valid @ModelAttribute ProductModifyForm productModifyForm, BindingResult bindingResult, HttpSession session) {
		Product product = this.productService.getProduct(productLink);
		model.addAttribute("productLink",productLink);
		model.addAttribute("product",product);
		model.addAttribute("productModifyForm", productModifyForm);
		String socialId = checkSession(session);
		
		log.info("image = " +productModifyForm.getUploadedImage());
		
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("productModifyForm", productModifyForm);
			return "products/product_modify";
		}
		
		System.out.println(productLink);
		this.productService.modify(productModifyForm.getName(), productModifyForm.getDescription(), productModifyForm.getUploadedImage(), productModifyForm.getOptionDetail(), socialId, productLink);
		
		System.out.println(productLink);
	    return "redirect:/products/{product_link}"; 
	}


	@GetMapping("/products/{product_link}/delete")
	public String deleteProduct(@PathVariable("product_link") String productLink, HttpSession session) {
	    try {
	        String socialId = checkSession(session);
	        Product product = productService.getProduct(productLink);
	        
	        if (!product.getWishlist().getMember().getSocialLoginId().equals(socialId)) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
	        }
	   
	        log.info("Deleting product link: " + productLink);
	        this.productService.delete(productLink,socialId);
	        
	        // 삭제 후 해당 상품이 속한 위시리스트의 상품 목록 페이지로 리다이렉트
	        return "redirect:/" + product.getWishlist().getLink() + "/products";
	    } catch (Exception e) {
	        log.error("상품 삭제 중 오류 발생: " + e.getMessage());
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상품 삭제 중 오류 발생", e);
	    }
	}


	private String checkSession(HttpSession session){
		if (session == null)
			throw new SessionNotFoundException(SESSION_NOT_FOUND);

		if (session.getAttribute("kakaoId") == null)
			throw new SessionNotFoundException(SESSION_NOT_FOUND);
		return session.getAttribute("kakaoId").toString();

    }
	
		        
    
	
}  

