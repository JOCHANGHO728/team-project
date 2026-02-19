package com.example.server.controller;

import com.example.server.dto.OrderHistoryDto;
import com.example.server.dto.OrderRequestDto;
import com.example.server.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 1. 주문 내역 전체 조회
    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryDto>> getOrderHistory() {
        List<OrderHistoryDto> history = orderRepository.getOrderHistory();
        return ResponseEntity.ok(history);
    }

    // 2. 장바구니 상품 결제
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody OrderRequestDto orderRequest) {
        orderRepository.createOrder(orderRequest);
        return ResponseEntity.ok("결제가 성공적으로 처리되었습니다.");
    }
}
