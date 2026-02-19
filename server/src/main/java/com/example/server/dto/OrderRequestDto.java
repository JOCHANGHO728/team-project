package com.example.server.dto;

import java.util.List;

public class OrderRequestDto {
    private Long customerId;
    private List<CartItemDto> cartItems;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public List<CartItemDto> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItemDto> cartItems) { this.cartItems = cartItems; }
}
