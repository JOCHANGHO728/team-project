package com.example.server.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductUpdateDto {
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int pPrice;
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private int pQuantity;
}
