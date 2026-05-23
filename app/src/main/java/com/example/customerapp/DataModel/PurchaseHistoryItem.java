package com.example.customerapp.DataModel;

import com.google.gson.annotations.SerializedName;

public class PurchaseHistoryItem {
    @SerializedName(value = "orderId", alternate = {"order_id"})
    private Long orderId;

    @SerializedName(value = "orderDate", alternate = {"order_date"})
    private String orderDate;

    @SerializedName(value = "orderTotalAmount", alternate = {"order_total_amount", "totalAmount", "total_amount"})
    private int orderTotalAmount;

    @SerializedName(value = "uId", alternate = {"uid", "u_id", "login_id"})
    private String uId;

    @SerializedName(value = "uName", alternate = {"uname", "u_name", "userName", "user_name"})
    private String uName;

    @SerializedName(value = "pName", alternate = {"PName", "pname", "p_name", "productName", "product_name"})
    private String pName;

    @SerializedName(value = "category", alternate = {"pCategory", "p_category"})
    private String category;

    @SerializedName(value = "pPrice", alternate = {"PPrice", "pprice", "p_price", "productPrice", "product_price"})
    private int pPrice;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName(value = "lineTotal", alternate = {"line_total"})
    private int lineTotal;

    public Long getOrderId() { return orderId; }
    public String getOrderDate() { return orderDate; }
    public int getOrderTotalAmount() { return orderTotalAmount; }
    public String getUId() { return uId; }
    public String getUName() { return uName; }
    public String getPName() { return pName; }
    public String getDisplayPName() {
        return (pName == null || pName.isBlank()) ? "상품명 없음" : pName;
    }
    public String getCategory() { return category; }
    public int getPPrice() { return pPrice; }
    public int getQuantity() { return quantity; }
    public int getLineTotal() { return lineTotal; }
}
