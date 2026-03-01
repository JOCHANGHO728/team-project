package com.example.server.controller;

import com.example.server.dto.ManagerLoginDto;
import com.example.server.dto.ProductCreateDto;
import com.example.server.dto.ProductUpdateDto;
import com.example.server.entity.Product;
import com.example.server.service.ManagerService;
import com.example.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/managers")
@RequiredArgsConstructor
public class ManagerApiController {

    private final ManagerService managerService;
    private final ProductService productService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody ManagerLoginDto request) {
        String message = managerService.login(request);
        return ResponseEntity.ok().body(message);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok().body(products);
    }

    @PostMapping("/products")
    public ResponseEntity<String> addProduct(@RequestBody ProductCreateDto request) {
        productService.createProduct(request);
        return ResponseEntity.ok().body("상품 등록 완료");
    }

    @PutMapping("/products/{pId}")
    public ResponseEntity<String> updateProduct(@PathVariable Long pId, @RequestBody ProductUpdateDto request) {
        productService.updateProduct(pId, request);
        return ResponseEntity.ok().body("상품 수정 완료");
    }

    @DeleteMapping("/products/{pId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long pId) {
        productService.deleteProduct(pId);
        return ResponseEntity.ok().body("상품 삭제 완료");
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam("name") String keyword) {
        List<Product> products = productService.searchProductsByName(keyword);
        return ResponseEntity.ok().body(products);
    }
}