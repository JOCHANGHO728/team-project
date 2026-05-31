package com.example.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateDto {

    @JsonProperty("pName")
    @NotBlank(message = "상품명은 필수입니다.")
    private String pName;

    @JsonProperty("pPrice")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int pPrice;

    @JsonProperty("pQuantity")
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
    private int pQuantity;

    @JsonProperty("bKey")
    @NotBlank(message = "바코드 키는 필수입니다.")
    private String bKey;

    @JsonProperty("category")
    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;
}
