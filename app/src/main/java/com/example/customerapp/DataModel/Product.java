package com.example.customerapp.DataModel;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("pId")
    private Long pId;

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

    public Long getPId() { return pId; }
    public String getPName() { return pName; }
    public int getPPrice() { return pPrice; }
    public int getPQuantity() { return pQuantity; }
    public String getBKey() { return bKey; }
    public String getCategory() { return category; }

    // 장바구니용 수량 (서버 필드와 별개)
    private int cartQuantity = 1;
    public int getCartQuantity() { return cartQuantity; }
    public void setCartQuantity(int qty) { cartQuantity = qty; }
}
