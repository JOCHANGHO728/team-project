package com.example.server.dto;

import com.example.server.entity.OrderDetail;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PurchaseHistoryDto {

    private Long orderId;            // 주문 번호
    private LocalDateTime orderDate; // 결제(주문) 일시
    private int orderTotalAmount;    // 해당 주문의 총 결제액

    private String uId;              // 유저 로그인 ID
    private String uName;            // 유저 이름

    private String pName;            // 상품 이름
    private String category;         // 상품 카테고리
    private int pPrice;              // 상품 단가

    private int quantity;            // 구매 수량
    private int lineTotal;           // 해당 상품 총 결제액 (단가 * 수량)

    public static PurchaseHistoryDto from(OrderDetail detail) {
        return PurchaseHistoryDto.builder()
                .orderId(detail.getOrder().getOrderId())
                .orderDate(detail.getOrder().getOrderDate())
                .orderTotalAmount(detail.getOrder().getTotalAmount())
                .uId(detail.getOrder().getUser().getUId())
                .uName(detail.getOrder().getUser().getUName())
                .pName(detail.getProduct().getPName())
                .category(detail.getProduct().getCategory())
                .pPrice(detail.getProduct().getPPrice())
                .quantity(detail.getQuantity())
                .lineTotal(detail.getProduct().getPPrice() * detail.getQuantity()) // 단가 * 수량 자동 계산
                .build();
    }
}