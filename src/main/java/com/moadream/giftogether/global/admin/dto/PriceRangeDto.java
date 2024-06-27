package com.moadream.giftogether.global.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceRangeDto {
    String priceRange;
    int count;
}
