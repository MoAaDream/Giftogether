package com.moadream.giftogether.funding.model;

import java.time.LocalDateTime;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.product.model.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
@Table(name = "T_Funding")
public class Funding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(nullable = false)
    private Integer amount;
    
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private Status status;

    private LocalDateTime createAt; 

    private LocalDateTime updateAt;

    
    @OneToOne(mappedBy = "funding") 
    private Message message; 
    
    
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; 
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;  
    
}
