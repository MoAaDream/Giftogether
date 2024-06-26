package com.moadream.giftogether.funding.model;

import java.time.LocalDateTime;

import com.moadream.giftogether.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FundingDetailsDTO {
	
    private Integer amount;
    private Status status;
    private String messageContent;
    private String giverNickname;
    private String receiverNickname; 
    private String fundingUid;          // Funding 테이블의 고유 ID
    private LocalDateTime createdAt;    // Funding 객체가 생성된 날짜
    private String productName;         // 연관된 제품의 이름
    private LocalDateTime deadline;        // 연관된 제품의 이름 
}