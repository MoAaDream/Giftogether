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
	private String buyerAddress;
	private String itemName;
	private String buyerName;
	private String buyerTel;

    private String productLink;

	@Builder
	public RequestPayDto(String fundingUid, Integer paymentAmount, String itemName, String buyerTel, String productLink, String buyerName,
			String buyerAddress) {
		this.fundingUid = fundingUid;
		this.paymentAmount = paymentAmount;
		this.buyerAddress = buyerAddress;
		this.buyerAddress = itemName;
		this.buyerAddress = buyerName;
		this.buyerAddress = buyerTel;
        this.productLink = productLink;
	}
 
 
}