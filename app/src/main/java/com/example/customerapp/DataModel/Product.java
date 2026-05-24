package com.example.customerapp.DataModel;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName(value = "pId", alternate = {"PId", "pid", "p_id"})
    private Long pId;

    @SerializedName(value = "pName", alternate = {"PName", "pname", "p_name"})
    private String pName;

    @SerializedName(value = "pPrice", alternate = {"PPrice", "pprice", "p_price"})
    private int pPrice;

    @SerializedName(value = "pQuantity", alternate = {"PQuantity", "pquantity", "p_quantity"})
    private int pQuantity;

    @SerializedName(value = "bKey", alternate = {"BKey", "bkey", "b_key"})
    private String bKey;

    @SerializedName("category")
    private String category;

    public Long getPId() { return pId; }
    public String getPName() { return pName; }
    public int getPPrice() { return pPrice; }
    public int getPQuantity() { return pQuantity; }
    public String getBKey() { return bKey; }
    public String getCategory() { return category; }

    // 장바구니용 수량 (서버 필드와 별개)
    private int cartQuantity = 1;
    public int getCartQuantity() { return cartQuantity; }
    public void setCartQuantity(int qty) { cartQuantity = qty; }


    // 상품 이미지 초기화
    public String getImageUrl() {
        // 서버에서 이미지 필드가 추가되면 여기에 매핑
        // 예: return this.imageUrl;

        // 현재는 테스트용 placeholder 이미지 사용
        return "https://via.placeholder.com/150?text=" + pName;
    }
}
