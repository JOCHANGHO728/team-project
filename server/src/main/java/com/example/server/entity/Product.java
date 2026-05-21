package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "productdb")
@Getter @Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private Long pId;

    @Column(name = "p_name")
    private String pName;

    @Column(name = "p_price")
    private int pPrice;

    @Column(name = "p_quantity")
    private int pQuantity;

    @Column(name = "b_key")
    private String bKey;

    private String category;
}