package com.example.server.service;

import com.example.server.dto.PurchaseHistoryDto;
import com.example.server.entity.Order;
import com.example.server.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseHistoryService {

    private final OrderRepository orderRepository;

    public List<PurchaseHistoryDto> getUserPurchaseHistory(String uId) {
        List<Order> orders = orderRepository.findLedgerByUserId(uId);
        return convertToDtoList(orders);
    }

    //  특정 기간(날짜) 구매 내역 조회
    public List<PurchaseHistoryDto> getUserPurchaseHistoryByDateRange(String uId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findLedgerByUserIdAndDateRange(uId, startDateTime, endDateTime);
        return convertToDtoList(orders);
    }

    public List<PurchaseHistoryDto> getAllPurchaseHistories() {
        List<Order> orders = orderRepository.findAllLedger();
        return convertToDtoList(orders);
    }

    private List<PurchaseHistoryDto> convertToDtoList(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getOrderDetails().stream())
                .map(PurchaseHistoryDto::from)
                .collect(Collectors.toList());
    }
}