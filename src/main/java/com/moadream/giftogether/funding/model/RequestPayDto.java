package com.moadream.giftogether.funding.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestPayDto {
    private String fundingUid; 
    private Integer paymentAmount;
    private String buyerEmail;
    private String buyerAddress;

    @Builder
    public RequestPayDto(String fundingUid,  Integer paymentAmount, String buyerEmail, String buyerAddress) {
        this.fundingUid = fundingUid; 
        this.paymentAmount = paymentAmount;
        this.buyerEmail = buyerEmail;
        this.buyerAddress = buyerAddress;
    }
}