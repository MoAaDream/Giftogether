package com.moadream.giftogether.wishlist;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.model.Role;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.model.WishListForm;
import com.moadream.giftogether.wishlist.repository.WishListRepository;
import com.moadream.giftogether.wishlist.service.WishListService;
import jakarta.persistence.*;
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
import java.util.Optional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class WishListServiceTest {

    @Autowired
    private WishListService wishListService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WishListRepository wishListRepository;


    @Test
    @DisplayName("위시리스트 생성 - 실패")
    public void createWishListFail() throws Exception {
        //given
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

        WishListForm wishListForm = WishListForm.builder()
                .name("위시리스트 제목")
                .description("위시리스트 설명")
                .deadLine(LocalDateTime.now().plus(4, ChronoUnit.DAYS))
                .imgLink(null)
                .address("위시리스트 주소")
                .phoneNumber("01011112222")
                .build();

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

        WishListForm wishListForm = WishListForm.builder()
                .name("위시리스트 제목")
                .description("위시리스트 설명")
                .deadLine(LocalDateTime.now().plus(4, ChronoUnit.DAYS))
                .imgLink(null)
                .address("위시리스트 주소")
                .phoneNumber("01011112222")
                .build();

        //when
        wishListService.createWishList(wishListForm, "3051424432");
        WishList wishList = wishListRepository.findByMember_Id(member.getId()).get();


        //then
        Assertions.assertThat(wishList.getListImg()).isEqualTo("main.png");
        Assertions.assertThat(wishListForm.getName()).isEqualTo(wishList.getName());

    }
}
