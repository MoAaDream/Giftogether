package com.moadream.giftogether.wishlist.service;

import static com.moadream.giftogether.member.exception.MemberExceptionCode.NOT_FOUND_SOCIAL_ID;
import static com.moadream.giftogether.wishlist.exception.WishlistExceptionCode.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.service.PaymentService;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.exception.MemberException;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.product.Repository.ProductRepository;
import com.moadream.giftogether.product.Service.ProductService;
import com.moadream.giftogether.product.model.Product;
import com.moadream.giftogether.wishlist.exception.WishListException;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishlistDto;
import com.moadream.giftogether.wishlist.repository.WishListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishListService implements WishListServiceI {

	private final WishListRepository wishListRepository;
	private final MemberRepository memberRepository;
	private final PaymentService paymentService;
	private final ProductService productService;
	private final ProductRepository productRepository;

	@Transactional
	public void createWishList(WishListForm wishListForm, String socialId) {
		Member member = findMemberBySocialId(socialId);

		if(wishListForm.getDeadLine().toLocalDate().isBefore(LocalDate.now())) {
			throw new WishListException(NOT_DEADLINE_CREATE);
		}
		System.out.println("time = " + LocalDate.now());
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
		List<WishlistDto> wishlists = wishListPage.stream().map(wishList -> new WishlistDto(wishList)).toList();

		return new PageImpl<>(wishlists, pageable, wishListPage.getTotalElements());
	}


    @Override
    @Transactional
    public void updateWishListStatus() {
        List<WishList> activeWishlist = wishListRepository.findAllByStatus(Status.A);
        LocalDateTime now = LocalDateTime.now();
        boolean changesMade = false;
        List<Product> productsToUpdate = new ArrayList<>();
        
        for (WishList wishList : activeWishlist) {
            if (wishList.getDeadline().isBefore(now)) {
                wishList.setStatus(Status.I);    // 위시리스트의 상태를 업데이트
                // 이 위시리스트에 연관된 모든 제품의 상태를 업데이트
                for (Product product : wishList.getProductList()) {
                    product.setStatus(Status.I);
                    productsToUpdate.add(product);
                }
                changesMade = true;
            }
        }
        // 변경 사항이 있으면 모든 위시리스트와 제품을 저장
        if (changesMade) {
            wishListRepository.saveAll(activeWishlist);
            productRepository.saveAll(productsToUpdate);   
        }
    

		for (WishList wishList : activeWishlist) {
			if (wishList.getDeadline().isBefore(now)) {
				wishList.setStatus(Status.I); // 위시리스트의 상태를 업데이트
				// 이 위시리스트에 연관된 모든 제품의 상태를 업데이트
				for (Product product : wishList.getProductList()) {
					product.setStatus(Status.I);
					productsToUpdate.add(product);
				}
				changesMade = true;
			}
		}
		// 변경 사항이 있으면 모든 위시리스트와 제품을 저장
		if (changesMade) {
			wishListRepository.saveAll(activeWishlist);
			productRepository.saveAll(productsToUpdate);
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

	@Override
	@Transactional
	public void deleteWishlistForExistFunding(String socialId, String wishlistLink) {
		Member member = findMemberBySocialId(socialId);
		WishList wishList = findWishListByLink(wishlistLink);

		checkMyWishList(wishList, member);

		wishList.getProductList().stream().forEach(product -> {
			paymentService.productCancel(product.getProductLink(), socialId);
			productService.delete(product.getProductLink(), socialId);
		});

		wishListRepository.deleteById(wishList.getId());
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
		return wishListRepository.findByLink(wishlistLink).orElseThrow(() -> new WishListException(NOT_FOUND_WISHLIST));
	}

	private static void checkFundingExist(WishList wishList) {
		wishList.getProductList().stream().flatMap(product -> product.getFundingList().stream())
				.filter(funding -> funding.getStatus() == Status.A).findAny().ifPresent(funding -> {
					throw new WishListException(NOT_DELETE_WISHLIST_BY_FUNDING);
				});
	}
}
