package com.moadream.giftogether.bank.model;

import com.moadream.giftogether.global.audit.BaseTimeEntity;
import com.moadream.giftogether.member.model.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "T_Bank")
public class Bank extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String bankName;

    @Column(nullable = false, length = 50)
    private String account;

    @Column(nullable = false)
    private Integer deposit;

    @Column(nullable = false)
    private String productLink;
    
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    public static Bank createBank(BankForm bankForm, Member member, int currentAmount, String productLink){
    	Bank bank = new Bank();
    	bank.bankName = bankForm.getBankName();// 은행 이름
    	bank.account = bankForm.getAccount(); // 계좌 번호
    	bank.deposit = currentAmount;
    	bank.productLink = productLink;
    	bank.member = member;
 
		return bank;
	}
}
