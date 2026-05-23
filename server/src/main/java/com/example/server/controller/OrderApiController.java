package com.example.server.controller;

import com.example.server.common.ApiResponse;
import com.example.server.dto.OrderCreateDto;
import com.example.server.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<Void> createOrder(
            @RequestAttribute("authenticatedUserId") String authenticatedUserId,
            @Valid @RequestBody OrderCreateDto request) {
        // IDOR 방어: 로그인된 사용자의 ID를 DTO에 강제 설정
        request.setUId(authenticatedUserId);
        orderService.createOrder(request);
        return ApiResponse.success("주문이 성공적으로 완료되었습니다.", null);
    }
}
