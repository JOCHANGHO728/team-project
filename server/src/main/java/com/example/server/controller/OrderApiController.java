package com.example.server.controller;

import com.example.server.dto.OrderCreateDto;
import com.example.server.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderCreateDto request) {
        try {
            orderService.createOrder(request);
            return ResponseEntity.ok("주문이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}