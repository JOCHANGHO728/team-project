package com.example.server.controller;

import com.example.server.common.ApiResponse;
import com.example.server.dto.PurchaseHistoryDto;
import com.example.server.service.PurchaseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/purchase-history")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    // 예시: GET https://server-jc54.onrender.com/api/v1/purchase-history/user_001
    @GetMapping("/{uId}")
    public ApiResponse<List<PurchaseHistoryDto>> getUserPurchaseHistory(@PathVariable String uId) {
        List<PurchaseHistoryDto> historyList = purchaseHistoryService.getUserPurchaseHistory(uId);
        return ApiResponse.success("구매 내역 조회 성공", historyList);
    }

    // 예시: GET https://server-jc54.onrender.com/api/v1/purchase-history/user_001/range?startDate=2026-03-01&endDate=2026-04-30
    @GetMapping("/{uId}/range")
    public ApiResponse<List<PurchaseHistoryDto>> getUserPurchaseHistoryByDateRange(
            @PathVariable String uId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PurchaseHistoryDto> historyList = purchaseHistoryService.getUserPurchaseHistoryByDateRange(uId, startDate, endDate);
        return ApiResponse.success("기간별 구매 내역 조회 성공", historyList);
    }

    // 예시: GET https://server-jc54.onrender.com/api/v1/purchase-history/all
    @GetMapping("/all")
    public ApiResponse<List<PurchaseHistoryDto>> getAllPurchaseHistories() {
        List<PurchaseHistoryDto> historyList = purchaseHistoryService.getAllPurchaseHistories();
        return ApiResponse.success("전체 구매 내역 조회 성공", historyList);
    }
}
