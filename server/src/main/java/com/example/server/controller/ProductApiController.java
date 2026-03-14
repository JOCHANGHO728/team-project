package com.example.server.controller;

import com.example.server.dto.ProductResponseDto;
import com.example.server.entity.Product;
import com.example.server.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductRepository productRepository;

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProduct(@RequestParam("keyword") String keyword) {

        List<Product> products = productRepository.searchByName(keyword);


        List<ProductResponseDto> responseDtos = products.stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());


        return ResponseEntity.ok(responseDtos);
    }
}
// 이름으로 제품 검색하는 컨트롤러