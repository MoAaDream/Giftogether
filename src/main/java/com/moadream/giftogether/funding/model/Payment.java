package com.moadream.giftogether.funding.model;

import com.moadream.giftogether.global.audit.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "T_Payment")
public class Payment extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Integer amount;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	@Column(length = 30, unique = true)
	private String paymentUid; // 결제 고유 번호

	@OneToOne(mappedBy = "payment")
	private Funding funding;

	@Builder
	public Payment(Integer amount, PaymentStatus status, Funding funding) {
		this.amount = amount;
		this.status = status;
		this.funding = funding;
	}

	// setter
	public void changePaymentStatus(PaymentStatus status, String paymentUid) {
		this.status = status;
		this.paymentUid = paymentUid;
	}
	

}