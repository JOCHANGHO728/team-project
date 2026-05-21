package com.example.server.dto;

import com.example.server.entity.Product;
import lombok.Getter;

@Getter
public class ProductResponseDto {
    private Long pId;
    private String pName;
    private int pPrice;
    private int pQuantity;
    private String bKey;      // 수정: 누락된 bKey(바코드) 필드 추가
    private String category;

    public ProductResponseDto(Product product) {
        this.pId = product.getPId();
        this.pName = product.getPName();
        this.pPrice = product.getPPrice();
        this.pQuantity = product.getPQuantity();
        this.bKey = product.getBKey();  // 수정: bKey 매핑 추가
        this.category = product.getCategory();
    }
}