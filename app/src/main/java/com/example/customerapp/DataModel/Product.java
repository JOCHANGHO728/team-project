package com.example.customerapp.DataModel;

public class Product {
    // 데이터 베이스 테이블 참조
    //p_id	p_name	p_price	p_quantity	b_key	category
    String id;
    String name;
    int price;
    int quantity;
    int b_key;
    int category;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
}