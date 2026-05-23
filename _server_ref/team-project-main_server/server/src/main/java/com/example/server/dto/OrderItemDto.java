package com.example.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemDto {
    @JsonProperty("p_id")
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long pId;
    @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다.")
    private int quantity;
}
