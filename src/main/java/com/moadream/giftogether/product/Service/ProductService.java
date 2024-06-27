package com.moadream.giftogether.product.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.moadream.giftogether.DataNotFoundException;
import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
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
	
	private final MemberRepository memberRepository;
	
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
 
	public void modify(String name, String description, String productImg , String optionDetail, String socialId, String productLink) {
		Member member = findMemberBySocialId(socialId);
		Product product = findProductByProductLink(productLink);
		
		checkMyProduct(product, member);
		
		product.setName(name);
		product.setDescription(description);
		product.setProductImg(productImg);
		product.setOptionDetail(optionDetail);
		productRepository.save(product);
		
	}
	
	private Member findMemberBySocialId(String socialId) {
		Optional<Member> member = memberRepository.findBySocialLoginId(socialId);
		if (member.isPresent()) {
            return member.get();
        } else {
            throw new DataNotFoundException("member not found");
        }
	}
	
	private Product findProductByProductLink(String productLink) {
		Optional<Product> product = productRepository.findByProductLink(productLink);
		if (product.isPresent()) {
			return product.get();
	    } else {
	        throw new DataNotFoundException("product not found");
	    }
	
	}
	


	private void checkMyProduct(Product product, Member member) {
        if(!product.getWishlist().getMember().getId().equals(member.getId()))
            throw new DataNotFoundException("수정권한 없음");
    }

	public void delete(String productLink,String socialId) {
		Member member = findMemberBySocialId(socialId);
		Product product = findProductByProductLink(productLink);
		
		checkMyProduct(product, member);
	   
        if (product.getCurrentAmount() > 0) {
            throw new IllegalArgumentException("진행중인 펀딩은 삭제할 수 없습니다");
        }else {
        	System.out.println(9);
        	productRepository.deleteById(product.getId());
        }
	}

	private Product findByLink(String productLink) {
		// TODO Auto-generated method stub
		return null;
	}
	
	


}