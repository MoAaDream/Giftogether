package com.moadream.giftogether.funding;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moadream.giftogether.product.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	
}
