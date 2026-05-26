package com.example.server.dto;

import com.example.server.entity.Product;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductResponseDtoTest {

    @Test
    void includesImageUrlFromProduct() {
        Product product = new Product();
        product.setImageUrl("https://res.cloudinary.com/example/product.webp");

        ProductResponseDto response = new ProductResponseDto(product);

        assertThat(response.getImageUrl())
                .isEqualTo("https://res.cloudinary.com/example/product.webp");
    }
}
