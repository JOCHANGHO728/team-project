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
    public ApiResponse<List<PurchaseHistoryDto>> getUserPurchaseHistory(
            @RequestAttribute("authenticatedUserId") String authenticatedUserId,
            @PathVariable String uId) {
        // IDOR 방어: 자신의 구매 내역만 조회 가능하도록 제한
        if (!uId.equals(authenticatedUserId)) {
            throw new IllegalArgumentException("자신의 구매 내역만 조회할 수 있습니다.");
        }
        List<PurchaseHistoryDto> historyList = purchaseHistoryService.getUserPurchaseHistory(uId);
        return ApiResponse.success("구매 내역 조회 성공", historyList);
    }

    // 예시: GET https://server-jc54.onrender.com/api/v1/purchase-history/user_001/range?startDate=2026-03-01&endDate=2026-04-30
    @GetMapping("/{uId}/range")
    public ApiResponse<List<PurchaseHistoryDto>> getUserPurchaseHistoryByDateRange(
            @RequestAttribute("authenticatedUserId") String authenticatedUserId,
            @PathVariable String uId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        // IDOR 방어: 자신의 구매 내역만 조회 가능하도록 제한
        if (!uId.equals(authenticatedUserId)) {
            throw new IllegalArgumentException("자신의 구매 내역만 조회할 수 있습니다.");
        }
        List<PurchaseHistoryDto> historyList = purchaseHistoryService.getUserPurchaseHistoryByDateRange(uId, startDate, endDate);
        return ApiResponse.success("기간별 구매 내역 조회 성공", historyList);
    }
}
