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
    private String fundingUid;          
    private LocalDateTime createdAt;   
    private String productName;         
    private LocalDateTime deadline;     
    private boolean canViewDetails; // 요청한사람과 펀딩한사람 일치 => true
    private boolean successFunding; 
	private String dDay;
}