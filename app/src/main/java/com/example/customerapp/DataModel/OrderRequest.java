package com.example.customerapp.DataModel;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderRequest {
    @SerializedName("login_id")
    private String uId;

    @SerializedName("items")
    private List<OrderItem> items;

    public OrderRequest(String uId, List<OrderItem> items) {
        this.uId = uId;
        this.items = items;
    }
}
