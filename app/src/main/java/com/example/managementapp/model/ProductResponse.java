package com.example.managementapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductResponse implements Serializable {

    @SerializedName(value = "pId", alternate = {"pid", "p_id"})
    private Long p_id;

    @SerializedName(value = "pName", alternate = {"pname", "p_name", "name"})
    private String p_name;

    @SerializedName(value = "pPrice", alternate = {"pprice", "p_price", "price"})
    private int p_price;

    @SerializedName(value = "pQuantity", alternate = {"pquantity", "p_quantity", "quantity"})
    private int p_quantity;

    @SerializedName(value = "bKey", alternate = {"bkey", "b_key", "barcode"})
    private String b_key;

    @SerializedName("category")
    private String category;

    @SerializedName(value = "imageUrl", alternate = {"imageurl", "image_url"})
    private String imageUrl;

    public Long getP_id() {
        return p_id;
    }

    public String getP_name() {
        return p_name;
    }

    public int getP_price() {
        return p_price;
    }

    public int getP_quantity() {
        return p_quantity;
    }

    public String getB_key() {
        return b_key;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "p_id=" + p_id +
                ", p_name='" + p_name + '\'' +
                ", p_price=" + p_price +
                ", p_quantity=" + p_quantity +
                ", b_key='" + b_key + '\'' +
                ", category='" + category + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
