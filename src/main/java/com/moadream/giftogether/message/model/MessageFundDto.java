package com.moadream.giftogether.message.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageFundDto {

    private Long memberId;

    private String name;

    private String content;

    private int amount;

    private String fundingUID;

}
