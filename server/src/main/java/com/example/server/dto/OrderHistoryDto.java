package com.yourname.shoppingcart.dto; // 본인의 패키지 경로로 수정해주세요!

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

    // 생성자 (Repository에서 DB 값을 이 객체로 옮겨 담을 때 사용)
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

    // Getter 메서드들 (이게 있어야 Spring이 JSON으로 변환할 수 있습니다)
    public Long getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getItemTotalPrice() { return itemTotalPrice; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}