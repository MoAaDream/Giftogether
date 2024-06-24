package com.moadream.giftogether.wishlist;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.model.Role;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.model.WishListModifyForm;
import com.moadream.giftogether.wishlist.repository.WishListRepository;
import com.moadream.giftogether.wishlist.service.WishListServiceI;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class WishListServiceTest {

    @Autowired
    private WishListServiceI wishListService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WishListRepository wishListRepository;

    public Member createMember(){
        Member member = new Member();
        member.setSocialLoginId("3051424432");
        member.setSocialProvider("k");
        member.setNickname("테스트닉네임");
        member.setProfile("main.png");
        member.setBirth(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        member.setPhoneNumber("01011112222");
        member.setAddress("test");
        member.setRole(Role.MEMBER);
        member.setStatus(Status.A);
        memberRepository.save(member);

        return member;
    }

    public WishListForm createWishlistForm(){
        WishListForm wishListForm = WishListForm.builder()
                .name("위시리스트 제목")
                .description("위시리스트 설명")
                .deadLine(LocalDateTime.now().plus(4, ChronoUnit.DAYS))
                .imgLink(null)
                .address("위시리스트 주소")
                .phoneNumber("01011112222")
                .build();

        return wishListForm;
    }

    @Test
    @DisplayName("위시리스트 생성 - 실패")
    public void createWishListFail() throws Exception {
        //given
        Member member = createMember();
        WishListForm wishListForm = createWishlistForm();

        //when
        Assertions.assertThatThrownBy(
                () -> wishListService.createWishList(wishListForm, "000000000")
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("위시리스트 생성 테스트 - 성공")
    @Transactional
    public void createWishListSuccess() throws Exception {
        //given
        Member member = createMember();
        WishListForm wishListForm = createWishlistForm();

        //when
        wishListService.createWishList(wishListForm, member.getSocialLoginId());
        WishList wishList = wishListRepository.findByMember_Id(member.getId()).get();


        //then
        Assertions.assertThat(wishList.getListImg()).isEqualTo("main.png");
        Assertions.assertThat(wishListForm.getName()).isEqualTo(wishList.getName());

    }

    @Test
    @DisplayName("위시 리스트 수정 - 성공")
    @Transactional
    public void modifyWishListSuccess() throws Exception {
        //given
        Member member = createMember();
        WishListForm wishListForm = createWishlistForm();

        //when
        wishListService.createWishList(wishListForm, member.getSocialLoginId());
        WishList wishList = wishListRepository.findByMember_Id(member.getId()).get();

        String modifyName = "수정된 이름";
        String modifyDes = "수정된 내용";


        WishListModifyForm wishListModifyForm = WishListModifyForm.builder()
                .name(modifyName)
                .description(modifyDes)
                .address(wishListForm.getAddress())
                .phoneNumber(wishListForm.getPhoneNumber())
                .build();

        wishListService.modifyWishList(wishListModifyForm, member.getSocialLoginId(), wishList.getLink());

        //then
        Assertions.assertThat(wishList.getName()).isEqualTo(modifyName);
        Assertions.assertThat(wishList.getDescription()).isEqualTo(modifyDes);

    }
}
