package com.moadream.giftogether.wishlist.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishListModifyForm;
import com.moadream.giftogether.wishlist.repository.WishListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishListService implements WishListServiceI {

    private final WishListRepository wishListRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void createWishList(WishListForm wishListForm, String socialId) {
        Member member = findMemberBySocialId(socialId);

        WishList wishList = WishList.createWishList(wishListForm, member);

        wishListRepository.save(wishList);
        log.info("SERVICE = [" + socialId + "]" + "새 위시리스트 생성");
    }

    @Override
    @Transactional
    public void modifyWishList(WishListModifyForm wishListModifyForm, String socialId, String wishlistLink) {
        Member member = findMemberBySocialId(socialId);
        WishList wishList = findWishListByLink(wishlistLink);

        checkMyWishList(wishList, member);

        wishList.modifyWishList(wishListModifyForm);
    }

    @Override
    @Transactional
    public void deleteWishList(String socialId, String wishlistLink) {
        Member member = findMemberBySocialId(socialId);
        WishList wishList = findWishListByLink(wishlistLink);

        checkMyWishList(wishList, member);

        wishListRepository.deleteById(wishList.getId());
    }

    @Override
    @Transactional
    public Page<WishList> getList(String socialId, int page) {
        Member member = findMemberBySocialId(socialId);
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.ASC, "id"));

        return wishListRepository.findAllByMember_Id(member.getId(), pageable);
    }

        @Override
        public void updateWishListStatus() {
            List<WishList> activeWishlist = wishListRepository.findAllByStatus(Status.A);
            LocalDateTime now = LocalDateTime.now();
            for (WishList wishList : activeWishlist) {
                if(wishList.getDeadline().isBefore(now)){
                    wishList.setStatus(Status.I);
                }
            }
        }

    private void checkMyWishList(WishList wishList, Member member) {
        if(!wishList.getMember().getId().equals(member.getId()))
            throw new RuntimeException();
    }


    private Member findMemberBySocialId(String socialId) {
        return memberRepository.findBySocialLoginId(socialId)
                .orElseThrow(() -> new RuntimeException());
    }

    private WishList findWishListByLink(String wishlistLink){
        return wishListRepository.findByLink(wishlistLink)
                .orElseThrow(() -> new RuntimeException());
    }
    
    
    
}
