package com.example.server.service;

import com.example.server.dto.PurchaseHistoryDto;
import com.example.server.entity.Order;
import com.example.server.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseHistoryService {

    private final OrderRepository orderRepository;

    /**
     * 특정 유저(uId)의 구매 내역 조회
     */
    public List<PurchaseHistoryDto> getUserPurchaseHistory(String uId) {
        List<Order> orders = orderRepository.findLedgerByUserId(uId);
        return convertToDtoList(orders);
    }

    /**
     * 전체 유저의 구매 내역 조회 (관리자용)
     */
    public List<PurchaseHistoryDto> getAllPurchaseHistories() {
        List<Order> orders = orderRepository.findAllLedger();
        return convertToDtoList(orders);
    }

    /**
     * [공통 로직] Order 리스트를 PurchaseHistoryDto 리스트로 변환 (평탄화 작업)
     */
    private List<PurchaseHistoryDto> convertToDtoList(List<Order> orders) {
        return orders.stream()
                // 1. 하나의 주문(Order)에 담긴 여러 상세 내역(OrderDetail)을 꺼내서 일렬로 펼침(flatMap)
                .flatMap(order -> order.getOrderDetails().stream())
                // 2. 각각의 상세 내역을 DTO로 변환
                .map(PurchaseHistoryDto::from)
                // 3. 리스트로 묶어서 반환
                .collect(Collectors.toList());
    }
}