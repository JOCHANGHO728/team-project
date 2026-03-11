package com.example.server.dto;

import com.example.server.entity.Product;
import lombok.Getter;

@Getter
public class ProductResponseDto {
    private String pName;
    private int pPrice;
    private int pQuantity;
    private String category;

    
    public ProductResponseDto(Product product) {
        this.pName = product.getPName();
        this.pPrice = product.getPPrice();
        this.pQuantity = product.getPQuantity();
        this.category = product.getCategory();
    }
}