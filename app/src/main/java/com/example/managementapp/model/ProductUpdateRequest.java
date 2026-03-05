package com.example.managementapp.model;
import com.google.gson.annotations.SerializedName;

public class ProductUpdateRequest {
    @SerializedName("pPrice")
    private int pPrice;
    @SerializedName("pQuantity")
    private int pQuantity;

    public ProductUpdateRequest(int pPrice, int pQuantity) {
        this.pPrice = pPrice;
        this.pQuantity = pQuantity;
    }
}