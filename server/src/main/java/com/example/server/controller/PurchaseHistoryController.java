package com.example.server.controller;

import com.example.server.dto.PurchaseHistoryDto;
import com.example.server.service.PurchaseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-history")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    /**
     * 특정 회원의 구매 내역 조회 API
     * [GET] /api/purchase-history/{uId}
     * 예시: GET http://localhost:8080/api/purchase-history/user_001
     */
    @GetMapping("/{uId}")
    public ResponseEntity<List<PurchaseHistoryDto>> getUserPurchaseHistory(@PathVariable String uId) {
        List<PurchaseHistoryDto> historyList = purchaseHistoryService.getUserPurchaseHistory(uId);

        // 데이터가 비어있어도 200 OK와 함께 빈 배열([])을 반환하는 것이 일반적입니다.
        return ResponseEntity.ok(historyList);
    }

    /**
     * 전체 회원의 구매 내역 조회 API (관리자용)
     * [GET] /api/purchase-history/all
     * 예시: GET http://localhost:8080/api/purchase-history/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<PurchaseHistoryDto>> getAllPurchaseHistories() {
        List<PurchaseHistoryDto> historyList = purchaseHistoryService.getAllPurchaseHistories();
        return ResponseEntity.ok(historyList);
    }
}