package com.example.managementapp.model;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductResponse implements Serializable {

    //p_id	p_name	p_price	p_quantity	b_key	category

    @SerializedName("pId")
    private Long p_id;

    @SerializedName("pName")
    private String p_name;

    @SerializedName("pPrice")
    private int p_price;

    @SerializedName("pQuantity")
    private int p_quantity;

    @SerializedName("bKey")
    private String b_key;

    @SerializedName("category")
    private String category;

    // 🔥 Getter 추가
    public Long getP_id() { return p_id; }
    public String getP_name() { return p_name; }
    public int getP_price() { return p_price; }
    public int getP_quantity() { return p_quantity; }
    public String getB_key() { return b_key; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "p_id=" + p_id +
                ", p_name='" + p_name + '\'' +
                ", p_price=" + p_price +
                ", p_quantity=" + p_quantity +
                ", b_Key='" + b_key + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}