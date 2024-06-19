package com.moadream.giftogether.bank.model;

import java.time.LocalDateTime;

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
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String bankname;
    
    @Column(nullable = false, length = 30)
    private String account;
    
    @Column(nullable = false)
    private Integer deposit;
    
    private LocalDateTime createAt; 

    private LocalDateTime updateAt;    

    @ManyToOne
    @JoinColumn(name = "member_id")
 	private Member member; 

    
}
