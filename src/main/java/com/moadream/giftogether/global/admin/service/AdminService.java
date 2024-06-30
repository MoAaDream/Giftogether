package com.moadream.giftogether.global.admin.service;

import com.moadream.giftogether.funding.repository.PaymentRepository;
import com.moadream.giftogether.global.admin.dto.PriceRangeDto;
import com.moadream.giftogether.global.admin.dto.StaticsDto;
import com.moadream.giftogether.member.MemberRepository;
import com.moadream.giftogether.member.exception.MemberException;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.member.model.Role;
import com.moadream.giftogether.product.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.moadream.giftogether.member.exception.MemberExceptionCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;

    private final PaymentRepository paymentRepository;

    private final ProductRepository productRepository;
    /*
     * 총모금량 ,
     * 월별 매출(모금량),
     * 생성된 모금제품개수/ 완료된개수
     * 상품 가격대,
     */


    public void checkAdmin(String socialId) {
        Member member = memberRepository.findBySocialLoginId(socialId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_SOCIAL_ID));

        if (member.getRole() != Role.ADMIN)
            throw new MemberException(NO_AUTHORIZE_ADMIN);

    }

    @Transactional(readOnly = true)
    public StaticsDto getStatics() {
        StaticsDto staticsDto = new StaticsDto();
        staticsDto.setTotalAmount(getTotalAmount());
        staticsDto.setProductTotalCount(getTotalProductCount());
        staticsDto.setFinishedProductTotalCount(getFinishedProductCount());

        /*
         * - 50,000 ~ 100,000원
         * 100,001 ~ 200,000원
         * 200,001 ~ 400,000원
         * 400,001 ~ 800,000원
         * 800,001 ~ 1,600,000원
         * 1,600,001 ~ 3,200,000원
         * 3,200,001 ~ 5,000,000원
         * */
        List<PriceRangeDto> priceRangeDtos = new ArrayList<>();
        PriceRangeDto priceRangeDto1 = getPriceRange(50000, 10000);
        PriceRangeDto priceRangeDto2 = getPriceRange(100001, 200000);
        PriceRangeDto priceRangeDto3 = getPriceRange(200001, 400000);
        PriceRangeDto priceRangeDto4 = getPriceRange(400001, 800000);
        PriceRangeDto priceRangeDto5 = getPriceRange(800001, 1500000);
        PriceRangeDto priceRangeDto6 = getPriceRange(1500001, 3000000);
        PriceRangeDto priceRangeDto7 = getPriceRange(3000001, 5000000);
        priceRangeDtos.add(priceRangeDto1);
        priceRangeDtos.add(priceRangeDto2);
        priceRangeDtos.add(priceRangeDto3);
        priceRangeDtos.add(priceRangeDto4);
        priceRangeDtos.add(priceRangeDto5);
        priceRangeDtos.add(priceRangeDto6);
        priceRangeDtos.add(priceRangeDto7);

        staticsDto.setPriceRangeDtos(priceRangeDtos);

        return staticsDto;
    }

    @Transactional(readOnly = true)
    public List<Member> getBlackListMember(){
        return memberRepository.findAllByMisbehaviorCountGreaterThanEqual(5);
    }

    @Transactional
    public void removeBlackList(long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        member.removeBlackList();
    }

    //총 모금량 찾기
    private int getTotalAmount() {

        return paymentRepository.getTotalAmountByPaymentStatusO();
    }

    private int getTotalProductCount() {
        return productRepository.findAll().size();
    }

    private int getFinishedProductCount() {
        return productRepository.findFinishedCount();
    }


    private PriceRangeDto getPriceRange(int min, int max) {
        return new PriceRangeDto(min + "원 ~ " + max +"원", productRepository.getProductCountByAmountRange(min, max));
    }
}
