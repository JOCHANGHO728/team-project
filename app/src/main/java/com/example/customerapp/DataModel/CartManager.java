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
        addItem(product, 1, false);
    }

    public void addScannedItem(Product product, int quantity) {
        markScannedItem(product);
    }

    private void addItem(Product product, int quantity, boolean scanned) {
        if (product == null || product.getPId() == null) {
            return;
        }

        Product existing = findById(product.getPId());
        if (existing != null) {
            existing.setCartQuantity(existing.getCartQuantity() + quantity);
            if (scanned) {
                existing.setScannedQuantity(existing.getCartQuantity());
            }
            return;
        }

        product.setCartQuantity(quantity);
        product.setScannedQuantity(scanned ? quantity : 0);
        cartItems.add(product);
    }

    private void markScannedItem(Product product) {
        if (product == null || product.getPId() == null) {
            return;
        }

        Product existing = findById(product.getPId());
        if (existing != null) {
            existing.setScannedQuantity(existing.getCartQuantity());
            return;
        }

        product.setCartQuantity(1);
        product.setScannedQuantity(1);
        cartItems.add(product);
    }

    private Product findById(Long productId) {
        for (Product item : cartItems) {
            if (item.getPId() != null && item.getPId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    public List<Product> getCartItems() { return cartItems; }

    public List<Product> getScannedCartItems() {
        List<Product> scannedItems = new ArrayList<>();
        for (Product item : cartItems) {
            if (item.getScannedQuantity() > 0) {
                scannedItems.add(item);
            }
        }
        return scannedItems;
    }

    public void clearCart() { cartItems.clear(); }

    public void clearScannedItemsAfterPayment() {
        List<Product> remainingItems = new ArrayList<>();
        for (Product item : cartItems) {
            int remainingQuantity = item.getCartQuantity() - item.getScannedQuantity();
            if (remainingQuantity > 0) {
                item.setCartQuantity(remainingQuantity);
                item.setScannedQuantity(0);
                remainingItems.add(item);
            }
        }
        cartItems.clear();
        cartItems.addAll(remainingItems);
    }

    public void updateQuantity(Product product, int quantity) {
        if (product == null || product.getPId() == null) {
            return;
        }

        Product existing = findById(product.getPId());
        if (existing == null) {
            return;
        }

        if (quantity <= 0) {
            cartItems.remove(existing);
            return;
        }

        existing.setCartQuantity(quantity);
        if (existing.getScannedQuantity() > 0) {
            existing.setScannedQuantity(quantity);
        }
    }

    public void removeItem(Product product) {
        if (product == null || product.getPId() == null) {
            return;
        }

        Product existing = findById(product.getPId());
        if (existing != null) {
            cartItems.remove(existing);
        }
    }

    public int getTotalPrice() {
        int total = 0;
        for (Product item : cartItems) {
            total += item.getPPrice() * item.getCartQuantity();
        }
        return total;
    }

    public int getScannedTotalPrice() {
        int total = 0;
        for (Product item : cartItems) {
            total += item.getPPrice() * item.getScannedQuantity();
        }
        return total;
    }
}
