package com.example.customerapp.DataModel;

import com.google.gson.annotations.SerializedName;

public class OrderItem {
    @SerializedName("p_id")
    private Long pId;

    @SerializedName("quantity")
    private int quantity;

    public OrderItem(Long pId, int quantity) {
        this.pId = pId;
        this.quantity = quantity;
    }
}
