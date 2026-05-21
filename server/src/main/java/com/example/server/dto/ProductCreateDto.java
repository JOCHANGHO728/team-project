package com.example.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductCreateDto {
    private String pName;
    private int pPrice;
    private int pQuantity;
    private String bKey;
    private String category;
}