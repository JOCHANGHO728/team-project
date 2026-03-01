package com.example.server.service;

import com.example.server.dto.ProductCreateDto;
import com.example.server.dto.ProductUpdateDto;
import com.example.server.entity.Product;
import com.example.server.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 전체 상품 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 상품 이름 검색 메서드
    public List<Product> searchProductsByName(String keyword) {
        return productRepository.findByPNameContaining(keyword);
    }

    // 상품 추가
    @Transactional
    public void createProduct(ProductCreateDto request) {
        Product product = new Product();
        product.setPName(request.getPName());
        product.setPPrice(request.getPPrice());
        product.setPQuantity(request.getPQuantity());
        product.setBKey(request.getBKey());
        product.setCategory(request.getCategory());

        productRepository.save(product);
    }

    // 상품 수정
    @Transactional
    public void updateProduct(Long pId, ProductUpdateDto request) {
        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        product.setPPrice(request.getPPrice());
        product.setPQuantity(request.getPQuantity());
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long pId) {
        Product product = productRepository.findById(pId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        productRepository.delete(product);
    }
}