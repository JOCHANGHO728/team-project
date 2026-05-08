package com.example.managementapp.model;

import com.google.gson.annotations.SerializedName;

public class ProductUpdateRequest {
    @SerializedName("pPrice")
    private int pPrice;

    @SerializedName("pQuantity")
    private int pQuantity;

    @SerializedName("p_price")
    private int p_price;

    @SerializedName("p_quantity")
    private int p_quantity;

    @SerializedName("price")
    private int price;

    @SerializedName("quantity")
    private int quantity;

    public ProductUpdateRequest(int pPrice, int pQuantity) {
        this.pPrice = pPrice;
        this.pQuantity = pQuantity;
        this.p_price = pPrice;
        this.p_quantity = pQuantity;
        this.price = pPrice;
        this.quantity = pQuantity;
    }
}
