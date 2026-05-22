package com.example.managementapp.model;
import com.google.gson.annotations.SerializedName;

public class ProductCreateRequest {
    @SerializedName("pName")
    private String pName;
    @SerializedName("pPrice")
    private int pPrice;
    @SerializedName("pQuantity")
    private int pQuantity;
    @SerializedName("bKey")
    private String bKey;
    @SerializedName("category")
    private String category;

    public ProductCreateRequest(String pName, int pPrice, int pQuantity, String bKey, String category) {
        this.pName = pName;
        this.pPrice = pPrice;
        this.pQuantity = pQuantity;
        this.bKey = bKey;
        this.category = category;
    }
}
