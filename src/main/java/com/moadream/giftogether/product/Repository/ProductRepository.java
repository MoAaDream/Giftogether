package com.moadream.giftogether.product.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByProductLink(String productLink);

	List<Product> findByWishlistLink(String wishlistLink);

	@Query("SELECT count(p) FROM Product p WHERE p.currentAmount >= p.goalAmount")
	int findFinishedCount();

	@Query("SELECT COUNT(p) " + "FROM Product p " + "WHERE p.goalAmount BETWEEN :minAmount AND :maxAmount")
	int getProductCountByAmountRange(@Param("minAmount") int minAmount, @Param("maxAmount") int maxAmount);

	@Query("SELECT p.wishlist.member FROM Product p WHERE p.productLink = :productLink")
	Optional<Member> findMemberByProductLink(@Param("productLink") String productLink);
}