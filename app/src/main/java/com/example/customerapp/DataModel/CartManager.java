package com.example.customerapp.DataModel;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Product> cartItems = new ArrayList<>();
    private String loggedInUserId = "";
    private String accessToken = "";

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void setLoggedInUserId(String uId) { this.loggedInUserId = uId; }
    public String getLoggedInUserId() { return loggedInUserId; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getAccessToken() { return accessToken; }
    public String getAuthorizationHeader() {
        return (accessToken == null || accessToken.isBlank()) ? "" : "Bearer " + accessToken;
    }

    public void addItem(Product product) {
        for (Product item : cartItems) {
            if (item.getPId().equals(product.getPId())) {
                item.setCartQuantity(item.getCartQuantity() + 1);
                return;
            }
        }
        product.setCartQuantity(1);
        cartItems.add(product);
    }

    public List<Product> getCartItems() { return cartItems; }

    public void clearCart() { cartItems.clear(); }

    public int getTotalPrice() {
        int total = 0;
        for (Product item : cartItems) {
            total += item.getPPrice() * item.getCartQuantity();
        }
        return total;
    }
}
