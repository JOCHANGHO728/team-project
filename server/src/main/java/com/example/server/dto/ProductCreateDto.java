package com.example.server.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductCreateDto {
    @NotBlank(message = "상품명은 필수입니다.")
    private String pName;
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int pPrice;
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private int pQuantity;
    @NotBlank(message = "바코드 키는 필수입니다.")
    private String bKey;
    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;
}
