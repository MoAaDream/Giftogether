package com.moadream.giftogether.product.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.moadream.giftogether.product.Service.ProductService;
import com.moadream.giftogether.product.model.Product;
import com.moadream.giftogether.product.model.ProductForm;
import com.moadream.giftogether.wishlist.service.WishListService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
//@RequestMapping("/products")
public class ProductController {

	
	private final ProductService productService;
	private final WishListService wishlistService;
	
	@GetMapping("/{wishlist_link}/products")
    public String list(Model model,  @PathVariable("wishlist_link") String wishlistLink) {
		List<Product> productList = this.productService.getProductList(wishlistLink);
        model.addAttribute("productList", productList);
        model.addAttribute("wishlistLink",wishlistLink);
		return "product_list";
    }
	
	@GetMapping("/products/{product_link}")
	public String detail(Model model, @PathVariable("product_link") String productLink) {
	    Product product = this.productService.getProduct(productLink);
	    model.addAttribute("product", product);
	    return "product_detail";
	}
	
	@GetMapping("/{wishlist_link}/create")
	public String Create(ProductForm productForm, Model model) {
		model.addAttribute("productForm",productForm);
		return "product_form";
	}

	/*
	@PostMapping("/{wishlist_link}/create")
    public String Create(Model model,  @PathVariable("wishlist_link") String wishlistLink,
    		@RequestParam(value="name") String name, 
    		@RequestParam(value="description") String description, 
    		@RequestParam(value="externalLink") String externalLink, 
    		@RequestParam(value="uploadedImage") String productImg, 
    		@RequestParam(value="optionDetail",required=false) String optionDetail, 
    		@RequestParam(value="goalAmount") int goalAmount ) {
		String productLink = UUID.randomUUID().toString().replace("-", "");
		model.addAttribute("wishlistLink",wishlistLink);
		this.productService.create(name, description, externalLink, productImg, optionDetail, goalAmount, productLink, wishlistLink);
		
        return "redirect:/{wishlist_link}/products"; // 질문 저장후 질문목록으로 이동
    }*/
	
	@PostMapping("/{wishlist_link}/create")
	public String Create(Model model, @PathVariable("wishlist_link") String wishlistLink, @Valid @ModelAttribute ProductForm productForm, BindingResult bindingResult) {
		
		String productLink = UUID.randomUUID().toString().replace("-", "");
		model.addAttribute("wishlistLink",wishlistLink);
		
		if (bindingResult.hasErrors()) {
			return "product_form";
		}
		
		this.productService.create(productForm.getName(), productForm.getDescription(),productForm.getExternalLink(), 
				productForm.getUploadedImage(), productForm.getOptionDetail(), productForm.getGoalAmount(), productLink, wishlistLink);
		return "redirect:/{wishlist_link}/products"; // 질문 저장후 질문목록으로 이동
	}

	
		        
    
	
}  


