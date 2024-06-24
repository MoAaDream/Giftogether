package com.moadream.giftogether.funding;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moadream.giftogether.funding.model.Funding;

@Repository
public interface FundingRepository extends JpaRepository<Funding, Long> {
    @Query("select o from Funding o" +
            " left join fetch o.payment p" +
            " left join fetch o.member m" +
            " where o.fundingUid = :fundingUid")
    Optional<Funding> findFundingAndPaymentAndMember(@Param("fundingUid") String fundingUid);

    @Query("select o from Funding o" +
            " left join fetch o.payment p" +
            " where o.fundingUid = :fundingUid")
    Optional<Funding> findFundingAndPayment(@Param("fundingUid") String fundingUid);
}