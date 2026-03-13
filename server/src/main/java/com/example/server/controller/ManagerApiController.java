package com.example.server.controller;

import com.example.server.common.ApiResponse;
import com.example.server.dto.ManagerLoginDto;
import com.example.server.dto.ProductCreateDto;
import com.example.server.dto.ProductResponseDto;
import com.example.server.dto.ProductUpdateDto;
import com.example.server.service.ManagerService;
import com.example.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/managers")
@RequiredArgsConstructor
public class ManagerApiController {

    private final ManagerService managerService;
    private final ProductService productService;

    // 1. 관리자 로그인
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody ManagerLoginDto request) {
        String message = managerService.login(request);
        return ApiResponse.success("로그인 성공", message);
    }

    // 2. 전체 상품 조회
    @GetMapping("/products")
    public ApiResponse<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts().stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
        return ApiResponse.success("전체 상품 조회 성공", products);
    }

    // 3. 상품 등록
    @PostMapping("/products")
    public ApiResponse<Void> addProduct(@RequestBody ProductCreateDto request) {
        productService.createProduct(request);
        return ApiResponse.success("상품 등록 완료", null);
    }

    // 4. 상품 수정
    @PutMapping("/products/{pId}")
    public ApiResponse<Void> updateProduct(@PathVariable Long pId, @RequestBody ProductUpdateDto request) {
        productService.updateProduct(pId, request);
        return ApiResponse.success("상품 수정 완료", null);
    }

    // 5. 상품 삭제
    @DeleteMapping("/products/{pId}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long pId) {
        productService.deleteProduct(pId);
        return ApiResponse.success("상품 삭제 완료", null);
    }

    // 6. 상품 검색
    @GetMapping("/products/search")
    public ApiResponse<List<ProductResponseDto>> searchProducts(@RequestParam("name") String keyword) {
        List<ProductResponseDto> products = productService.searchProductsByName(keyword).stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
        return ApiResponse.success("상품 검색 성공", products);
    }
}