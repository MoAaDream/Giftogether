package com.moadream.giftogether.global.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaticsDto {

    int totalAmount;
    int productTotalCount;
    int finishedProductTotalCount;
    List<PriceRangeDto> priceRangeDtos;

}
