package com.moadream.giftogether.wishlist.service;


import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishListModifyForm;
import com.moadream.giftogether.wishlist.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if(wishList.getMember().getId() != member.getId())
            throw new RuntimeException();

        wishList.modifyWishList(wishListModifyForm);
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
