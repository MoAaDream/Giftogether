package com.moadream.giftogether.product.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class ProductForm {

	@NotEmpty(message="제품명은 필수항목입니다.")
    @Size(max=200)
    private String name;
    
    @NotEmpty(message="설명은 필수항목입니다.")
    private String description;
    
    @NotEmpty(message="구매링크는 필수항목입니다.")
    private String externalLink;
    
    @NotEmpty(message="사진등록은 필수항목입니다.")
    private String uploadedImage;
    
    private String optionDetail;
    
    @NotNull(message="금액은 필수항목입니다.")
    private int goalAmount;
    

}
