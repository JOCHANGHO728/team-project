package com.example.server.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderHistoryDto {
    private Long orderId;
    private String customerName;
    private LocalDateTime orderDate;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal itemTotalPrice;
    private BigDecimal totalAmount;

    public OrderHistoryDto(Long orderId, String customerName, LocalDateTime orderDate, 
                           String productName, Integer quantity, BigDecimal unitPrice, 
                           BigDecimal itemTotalPrice, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.itemTotalPrice = itemTotalPrice;
        this.totalAmount = totalAmount;
    }

    public Long getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getItemTotalPrice() { return itemTotalPrice; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}
