package com.moadream.giftogether.funding.model;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.global.audit.BaseTimeEntity;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.product.model.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_Funding")
public class Funding extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
 
    
    private String fundingUid; 

    
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
    
    @OneToOne(mappedBy = "funding")
    private Message message;

    @ManyToOne(fetch = FetchType. LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType. LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    
//    public static Funding cr(Integer amount, Member member, Product product) {
//    	Funding fd = new Funding();
//    	fd.amount = amount;
//    	fd.status = Status.I;
//    	fd.fundingUid = UUID.randomUUID().toString();
//    	fd.member = member;
//    	fd.product = product;
//    	
//    	
//    	return fd;
//    }
//    
//    public void updatePayment(Payment payment) {
//    	this.payment = payment;
//    }
}
