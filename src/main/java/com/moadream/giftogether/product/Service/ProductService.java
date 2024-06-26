package com.moadream.giftogether.product.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.moadream.giftogether.DataNotFoundException;
import com.moadream.giftogether.Status;
import com.moadream.giftogether.product.Repository.ProductRepository;
import com.moadream.giftogether.product.model.Product;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.repository.WishListRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
	
	private final WishListRepository wishListRepository;
	
	public List<Product>getList() {
		
		return this.productRepository.findAll();
	}
	
	public Product getProduct(long id) {  
        Optional<Product> product = this.productRepository.findById(id);
        if (product.isPresent()) {
            return product.get();
        } else {
            throw new DataNotFoundException("product not found");
        }
   
	}
	
	
	public Product getProduct(String productLink) {  
		Optional<Product> product = this.productRepository.findByProductLink(productLink);
        if (product.isPresent()) {
            return product.get();
        } else {
            throw new DataNotFoundException("product not found");
        }
   
	}
	
	public List<Product> getProductList(String wishlistLink){
		List<Product> productList = this.productRepository.findByWishlistLink(wishlistLink);
        return productList;
		
	}
	

	
	public void create(String name,String description,String externalLink, String productImg , String optionDetail, 
			int goalAmount, String productLink, String wishlistLink) {
		Product p = new Product();
		WishList wishlist = wishListRepository.findFirstByLink(wishlistLink).get();
		/*if (optionalWishList.isPresent()) {
		    WishList wishlist = optionalWishList.get();
		    p.setWishlist(wishlist);
		} else {
			throw new DataNotFoundException("wishlist sssss not found");
	    }*/
		p.setName(name);
		p.setDescription(description);
		p.setExternalLink(externalLink);
		p.setProductImg(productImg);
		p.setOptionDetail(optionDetail);
		p.setStatus(Status.A);
		p.setCurrentAmount(0);
		p.setGoalAmount(goalAmount);
		p.setProductLink(productLink);
		p.setWishlist(wishlist);
        productRepository.save(p);
    }
	


}