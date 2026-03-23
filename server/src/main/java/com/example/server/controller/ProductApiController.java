package com.example.server.controller;

import com.example.server.common.ApiResponse;
import com.example.server.dto.ProductResponseDto;
import com.example.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;

    // 이름으로 제품 검색하는 컨트롤러
    @GetMapping("/search")
    public ApiResponse<List<ProductResponseDto>> searchProduct(@RequestParam("keyword") String keyword) {
        try {
            List<ProductResponseDto> products = productService.searchProductsByName(keyword).stream()
                    .map(ProductResponseDto::new)
                    .collect(Collectors.toList());
            return ApiResponse.success("상품 검색 성공", products);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}