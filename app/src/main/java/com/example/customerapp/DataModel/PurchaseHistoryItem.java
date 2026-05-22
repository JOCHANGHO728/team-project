package com.example.customerapp.DataModel;

import com.google.gson.annotations.SerializedName;

public class PurchaseHistoryItem {
    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("orderDate")
    private String orderDate;

    @SerializedName("orderTotalAmount")
    private int orderTotalAmount;

    @SerializedName("uId")
    private String uId;

    @SerializedName("uName")
    private String uName;

    @SerializedName("pName")
    private String pName;

    @SerializedName("category")
    private String category;

    @SerializedName("pPrice")
    private int pPrice;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("lineTotal")
    private int lineTotal;

    public Long getOrderId() { return orderId; }
    public String getOrderDate() { return orderDate; }
    public int getOrderTotalAmount() { return orderTotalAmount; }
    public String getUId() { return uId; }
    public String getUName() { return uName; }
    public String getPName() { return pName; }
    public String getCategory() { return category; }
    public int getPPrice() { return pPrice; }
    public int getQuantity() { return quantity; }
    public int getLineTotal() { return lineTotal; }
}
