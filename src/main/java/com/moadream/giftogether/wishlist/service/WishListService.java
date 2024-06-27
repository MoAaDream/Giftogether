package com.moadream.giftogether.wishlist.service;


import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.exception.MemberException;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.wishlist.exception.WishListException;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishlistDto;
import com.moadream.giftogether.wishlist.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.moadream.giftogether.member.exception.MemberExceptionCode.NOT_FOUND_SOCIAL_ID;
import static com.moadream.giftogether.wishlist.exception.WishlistExceptionCode.*;

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
    public void modifyWishList(WishListForm wishListForm, String socialId, String wishlistLink) {
        Member member = findMemberBySocialId(socialId);
        WishList wishList = findWishListByLink(wishlistLink);

        checkMyWishList(wishList, member);

        wishList.modifyWishList(wishListForm);
    }

    @Override
    @Transactional
    public void deleteWishList(String socialId, String wishlistLink) {
        Member member = findMemberBySocialId(socialId);
        WishList wishList = findWishListByLink(wishlistLink);

        checkMyWishList(wishList, member);

        checkFundingExist(wishList);

        wishListRepository.deleteById(wishList.getId());
    }

    @Override
    @Transactional
    public Page<WishlistDto> getList(String socialId, int page) {
        Member member = findMemberBySocialId(socialId);
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.ASC, "id"));
        Page<WishList> wishListPage = wishListRepository.findAllByMember_Id(member.getId(), pageable);
        List<WishlistDto> wishlists = wishListPage.stream()
                .map(wishList -> new WishlistDto(wishList))
                .toList();

        return new PageImpl<>(wishlists, pageable, wishListPage.getTotalElements());
    }

    @Override
    public void updateWishListStatus() {
        List<WishList> activeWishlist = wishListRepository.findAllByStatus(Status.A);
        LocalDateTime now = LocalDateTime.now();
        for (WishList wishList : activeWishlist) {
            if (wishList.getDeadline().isBefore(now)) {
                wishList.setStatus(Status.I);
            }
        }
    }

    @Override
    public WishListForm getWishlist(String wishlistLink) {
        WishList wishlist = findWishListByLink(wishlistLink);

        return new WishListForm(wishlist);
    }

    @Override
    public boolean checkMyPage(String socialId, String wishlistLink) {
        Member member = findMemberBySocialId(socialId);
        WishList wishList = findWishListByLink(wishlistLink);

        if (!member.getId().equals(wishList.getMember().getId()))
            return false;

        return true;
    }

    private void checkMyWishList(WishList wishList, Member member) {
        if (!wishList.getMember().getId().equals(member.getId()))
            throw new WishListException(NOT_MY_WISHLIST);
    }


    private Member findMemberBySocialId(String socialId) {
        return memberRepository.findBySocialLoginId(socialId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_SOCIAL_ID));
    }

    private WishList findWishListByLink(String wishlistLink) {
        return wishListRepository.findByLink(wishlistLink)
                .orElseThrow(() -> new WishListException(NOT_FOUND_WISHLIST));
    }

    private static void checkFundingExist(WishList wishList) {
        wishList.getProductList().stream()
                .flatMap(product -> product.getFundingList().stream())
                .filter(funding -> funding.getStatus() == Status.A)
                .findAny()
                .ifPresent(funding -> {
                    throw new WishListException(NOT_DELETE_WISHLIST_BY_FUNDING);
                });
    }
}
