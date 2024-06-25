package com.moadream.giftogether.funding.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moadream.giftogether.product.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	//나중에 삭제 하고 옮기기
	Optional<Product> findByProductLink(String productLink);
}
