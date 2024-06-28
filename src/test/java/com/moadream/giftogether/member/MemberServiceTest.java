package com.moadream.giftogether.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.funding.model.Funding;
import com.moadream.giftogether.funding.repository.FundingRepository;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.service.MemberService;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.repository.WishListRepository;

@SpringBootTest
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    
    @Mock
    private FundingRepository fundingRepository;
    
    @Mock
    private WishListRepository wishListRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원 정보 가져오기 ")
    public void getMemberInfoSuccess() {
        // given
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setStatus(Status.A);

        when(memberRepository.findMemberByIdAndStatus(memberId)).thenReturn(member);

        // when
        Member result = memberService.getMemberInfo(memberId);

        // then
        assertThat(result.getId()).isEqualTo(memberId);
    }


    @Test
    @DisplayName("회원 삭제")
    public void softDeleteMemberSuccess() {
    	 // Given
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setStatus(Status.A); // Assuming A is Active

        Funding funding1 = new Funding();
        funding1.setId(1L);
        funding1.setStatus(Status.A);

        Funding funding2 = new Funding();
        funding2.setId(2L);
        funding2.setStatus(Status.A);
        
        WishList wishList1 = new WishList();
        wishList1.setId(1L);
        wishList1.setStatus(Status.A);

        WishList wishList2 = new WishList();
        wishList2.setId(2L);
        wishList2.setStatus(Status.A);

        List<WishList> wishLists = new ArrayList<>();
        wishLists.add(wishList1);
        wishLists.add(wishList2);
        
        member.setWishLists(wishLists);
        
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(fundingRepository.findAllByMember_Id(memberId)).thenReturn(List.of(funding1, funding2));
        when(wishListRepository.findAllByMember_Id(memberId)).thenReturn(wishLists);
        
        // When
        memberService.deleteMember(memberId);

        // Then
        verify(memberRepository, times(1)).findById(memberId);
        verify(fundingRepository, times(1)).findAllByMember_Id(memberId);

        // Verify soft delete for member and funding
        assertThat(member.getStatus()).isEqualTo(Status.D);
        assertThat(funding1.getStatus()).isEqualTo(Status.D);
        assertThat(funding2.getStatus()).isEqualTo(Status.D);

        // Verify hard delete for wishlists
        verify(wishListRepository, times(1)).deleteAll(wishLists);
    }
    
}
