package com.example.managementapp.model;
import com.google.gson.annotations.SerializedName;

public class ProductRequest {
    @SerializedName("pName")
    String pName;

    public ProductRequest(String pName) {
        this.pName = pName;
    }
}