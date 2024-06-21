package com.moadream.giftogether.wishlist.service;


import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishListService {

    private final WishListRepository wishListRepository;
    private final MemberRepository memberRepository;


    public void createWishList(WishListForm wishListForm, String socialId){
        Member member = memberRepository.findBySocialLoginId(socialId)
                .orElseThrow(() -> new RuntimeException());

        WishList wishList = WishList.createWishList(wishListForm, member);

        wishListRepository.save(wishList);
        log.info("SERVICE = [" + socialId + "]" + "새 위시리스트 생성");
    }

}
