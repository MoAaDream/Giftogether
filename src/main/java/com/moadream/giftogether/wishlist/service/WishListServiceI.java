package com.moadream.giftogether.wishlist.service;

import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishListModifyForm;

public interface WishListServiceI {

    public void createWishList(WishListForm wishListForm, String socialId);

    public void modifyWishList(WishListModifyForm wishListModifyForm, String socialId, String wishlistLink);
}
