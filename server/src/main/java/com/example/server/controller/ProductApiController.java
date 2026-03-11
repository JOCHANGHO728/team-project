package com.example.server.controller;

import com.example.server.entity.Product;
import com.example.server.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products") // 상품 관련 API는 이 주소로 시작합니다.
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductRepository productRepository;

    // 상품 이름으로 검색하는 API
    // 실행 주소: GET http://localhost:8080/api/v1/products/search?keyword=새우깡
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProduct(@RequestParam("keyword") String keyword) {
        // 리포지토리에 이미 만드신 searchByName 메서드를 호출합니다.
        List<Product> products = productRepository.searchByName(keyword);
        return ResponseEntity.ok(products);
    }
}