package com.moadream.giftogether.funding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moadream.giftogether.Status;
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
    
    

//    List<Funding> findByProductId(Long productId);
//    List<Funding> findByMemberId(Long memberId);
    
    @Query("SELECT f FROM Funding f JOIN FETCH f.member m JOIN FETCH f.product p JOIN FETCH p.wishlist w JOIN FETCH w.member WHERE f.product.id = :productId")
    List<Funding> findByProductIdWithDetails(@Param("productId") Long productId);
    
    @Query("SELECT f FROM Funding f JOIN FETCH f.member m JOIN FETCH f.product p JOIN FETCH p.wishlist w JOIN FETCH w.member WHERE m.socialLoginId = :socialId")
    List<Funding> findByMemberSocialIdWithDetails(@Param("socialId") String socialId);
    
    Funding findByFundingUid(String fundingUid);

    List<Funding> findAllByMember_Id(Long memberId);
    
    List<Funding> findAllByProductId(Long productId);
    
    List<Funding> findAllByProduct_ProductLinkAndStatus(String productLink, Status status);


}