package com.moadream.giftogether.wishlist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.wishlist.model.WishList;

public interface WishListRepository extends JpaRepository<WishList, Long> {

	Optional<WishList> findByMember_Id(Long memberId);

	Optional<WishList> findByLink(String wishlistLink);

	Optional<WishList> findFirstByLink(String wishlistLink);

	List<WishList> findAllByMember_Id(Long memberId);

	Page<WishList> findAllByMember_Id(Long memberId, Pageable pageable);

	List<WishList> findAllByStatus(Status status);

	List<WishList> findAllByMember_IdAndStatus(Long memberId, Status status);

}
