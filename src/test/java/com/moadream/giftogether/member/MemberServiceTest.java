package com.moadream.giftogether.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.service.MemberService;

@SpringBootTest
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원 정보 가져오기 - 성공")
    public void getMemberInfoSuccess() {
        // given
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setStatus(Status.A);

        when(memberRepository.findMemberById(memberId)).thenReturn(member);

        // when
        Member result = memberService.getMemberInfo(memberId);

        // then
        assertThat(result.getId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("회원 Hard Delete - 성공")
    public void deleteMemberSuccess() {
        // given
        Long memberId = 1L;

        // when
        memberService.deleteMember(memberId);

        // then
        verify(memberRepository, times(1)).deleteById(memberId);
    }

    @Test
    @DisplayName("회원 Soft Delete - 성공")
    public void softDeleteMemberSuccess() {
        // given
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setStatus(Status.A);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        memberService.softDeleteMember(memberId);

        // then
        assertThat(member.getStatus()).isEqualTo(Status.D);
        verify(memberRepository, times(1)).save(member);
    }
}
