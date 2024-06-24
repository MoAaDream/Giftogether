package com.moadream.giftogether.wishlist.repository;

import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.wishlist.model.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    Optional<WishList> findByMember_Id(Long memberId);

    Optional<WishList> findByLink(String wishlistLink);

    List<WishList> findAllByMember_Id(Long memberId);

    Page<WishList> findAllByMember_Id(Long memberId, Pageable pageable);
}
