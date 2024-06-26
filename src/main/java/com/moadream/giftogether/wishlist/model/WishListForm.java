package com.moadream.giftogether.wishlist.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishListForm {

    @NotBlank(message = "제목은 필수입니다.")
    private String name;

    @NotBlank(message = "설명은 필수입니다.")
    private String description;

    @NotNull(message = "기한 날짜는 필수입니다.")
    private LocalDateTime deadLine;

    private String uploadedImage;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotNull
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "10 ~ 11 자리의 숫자만 입력 가능합니다.")
    private String phoneNumber;

    private String link;

    public WishListForm(WishList wishList){
        this.link = wishList.getLink();
        this.name = wishList.getName();
        this.description = wishList.getDescription();
        this.uploadedImage = wishList.getListImg();
        this.address = wishList.getAddress();
        this.phoneNumber = wishList.getPhoneNumber();
    }
}
