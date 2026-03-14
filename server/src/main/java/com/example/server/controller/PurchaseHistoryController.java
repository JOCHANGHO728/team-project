package com.example.server.controller;

import com.example.server.dto.PurchaseHistoryDto;
import com.example.server.service.PurchaseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-history")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    // 예시: GET https://server-jc54.onrender.com/api/purchase-history/user_001
    @GetMapping("/{uId}")
    public ResponseEntity<List<PurchaseHistoryDto>> getUserPurchaseHistory(@PathVariable String uId) {
        List<PurchaseHistoryDto> historyList = purchaseHistoryService.getUserPurchaseHistory(uId);
        return ResponseEntity.ok(historyList);
    }

    // 예시: GET https://server-jc54.onrender.com/api/purchase-history/user_001/range?startDate=2026-03-01&endDate=2026-04-30
    @GetMapping("/{uId}/range")
    public ResponseEntity<List<PurchaseHistoryDto>> getUserPurchaseHistoryByDateRange(
            @PathVariable String uId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<PurchaseHistoryDto> historyList = purchaseHistoryService.getUserPurchaseHistoryByDateRange(uId, startDate, endDate);
        return ResponseEntity.ok(historyList);
    }

    // 예시: GET https://server-jc54.onrender.com/api/purchase-history/all
    @GetMapping("/all")
    public ResponseEntity<List<PurchaseHistoryDto>> getAllPurchaseHistories() {
        List<PurchaseHistoryDto> historyList = purchaseHistoryService.getAllPurchaseHistories();
        return ResponseEntity.ok(historyList);
    }
}