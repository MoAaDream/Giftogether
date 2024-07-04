package com.moadream.giftogether.funding.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FundingRequest {
    private String socialId;
    private String productLink;
    private Integer amount;
    private String message;
    private String fundingUid;   
    
    public FundingRequest(String socialId, String productLink, Integer amount, String message) {
        this.socialId = socialId;
        this.productLink = productLink;
        this.amount = amount;
        this.message = message; 
    }
}
