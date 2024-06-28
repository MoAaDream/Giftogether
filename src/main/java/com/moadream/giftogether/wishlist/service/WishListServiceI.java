package com.moadream.giftogether.wishlist.service;


import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishlistDto;
import org.springframework.data.domain.Page;

public interface WishListServiceI {

    public void createWishList(WishListForm wishListForm, String socialId);

    public void modifyWishList(WishListForm wishListForm, String socialId, String wishlistLink);


    public void deleteWishList(String socialId, String wishlistLink);

    public Page<WishlistDto> getList(String socialId, int page);

    public void updateWishListStatus();

    public WishListForm getWishlist(String wishlistLink);

    public boolean checkMyPage(String socialId, String wishlistLink);

    public void deleteWishlistForExistFunding(String socialId, String wishlistLink);

}
