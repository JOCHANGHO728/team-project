package com.example.server.repository;

import com.example.server.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.pName LIKE %:keyword%")
    List<Product> findByPNameContaining(@Param("keyword") String keyword);

}