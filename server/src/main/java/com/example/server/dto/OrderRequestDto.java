import java.util.List;

public class OrderRequestDto {
    private Long customerId;
    private List<CartItemDto> cartItems; // 장바구니에 담긴 여러 상품들

    // Getter & Setter
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public List<CartItemDto> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItemDto> cartItems) { this.cartItems = cartItems; }
}