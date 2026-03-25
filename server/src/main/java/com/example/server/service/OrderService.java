package com.example.server.service;

import com.example.server.dto.OrderCreateDto;
import com.example.server.dto.OrderItemDto;
import com.example.server.entity.Order;
import com.example.server.entity.OrderDetail;
import com.example.server.entity.Product;
import com.example.server.entity.User;
import com.example.server.repository.OrderRepository;
import com.example.server.repository.ProductRepository;
import com.example.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void createOrder(OrderCreateDto request) {

        // 수정: 주문 항목이 비어있는 경우 방어 코드 추가
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문 항목이 비어있습니다.");
        }

        User user = userRepository.findByUId(request.getUId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 아이디입니다."));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        int totalAmount = 0;

        for (OrderItemDto itemDto : request.getItems()) {

            // 수정: 수량이 0 이하인 경우 방어 코드 추가
            if (itemDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("주문 수량은 1 이상이어야 합니다.");
            }

            Product product = productRepository.findById(itemDto.getPId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. 상품 번호: " + itemDto.getPId()));

            if (product.getPQuantity() < itemDto.getQuantity()) {
                throw new IllegalArgumentException(product.getPName() + "의 재고가 부족합니다. (현재 재고: " + product.getPQuantity() + ")");
            }
            product.setPQuantity(product.getPQuantity() - itemDto.getQuantity());

            OrderDetail detail = new OrderDetail();
            detail.setProduct(product);
            detail.setQuantity(itemDto.getQuantity());

            order.addOrderDetail(detail);

            totalAmount += (product.getPPrice() * itemDto.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }
}