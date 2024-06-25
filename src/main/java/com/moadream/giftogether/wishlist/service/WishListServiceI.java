package com.moadream.giftogether.wishlist.service;


import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishListModifyForm;
import org.springframework.data.domain.Page;

public interface WishListServiceI {

    public void createWishList(WishListForm wishListForm, String socialId);

    public void modifyWishList(WishListModifyForm wishListModifyForm, String socialId, String wishlistLink);


    public void deleteWishList(String socialId, String wishlistLink);

    public Page<WishList> getList(String socialId, int page);

}
