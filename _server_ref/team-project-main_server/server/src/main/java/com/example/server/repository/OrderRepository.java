package com.example.server.repository;

import com.example.server.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 1. 특정 유저(uId)의 가계부 전체 내역 조회
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.user u " +
            "JOIN FETCH o.orderDetails od " +
            "JOIN FETCH od.product p " +
            "WHERE u.uId = :uId " +
            "ORDER BY o.orderDate DESC")
    List<Order> findLedgerByUserId(@Param("uId") String uId);

    // 2. 특정 유저(uId)의 특정 기간(날짜) 가계부 내역 조회
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.user u " +
            "JOIN FETCH o.orderDetails od " +
            "JOIN FETCH od.product p " +
            "WHERE u.uId = :uId " +
            "AND o.orderDate BETWEEN :startDate AND :endDate " +
            "ORDER BY o.orderDate DESC")
    List<Order> findLedgerByUserIdAndDateRange(
            @Param("uId") String uId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 3. (관리자용) 전체 회원의 가계부 내역 조회
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.user u " +
            "JOIN FETCH o.orderDetails od " +
            "JOIN FETCH od.product p " +
            "ORDER BY o.orderDate DESC")
    List<Order> findAllLedger();
}