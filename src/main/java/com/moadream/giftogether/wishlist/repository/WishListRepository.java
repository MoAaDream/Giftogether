package com.moadream.giftogether.wishlist.repository;

import com.moadream.giftogether.wishlist.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    Optional<WishList> findByMember_Id(Long memberId);

}
