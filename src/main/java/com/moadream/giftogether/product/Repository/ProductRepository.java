package com.moadream.giftogether.product.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moadream.giftogether.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByProductLink(String productLink);
	List<Product> findByWishlistLink(String wishlistLink);
}
