package com.example.customerapp.Customer.Shopping;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customerapp.DataModel.Product;
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityCategoryBinding;

import java.util.ArrayList;
import java.util.List;

public class Category extends AppCompatActivity {
    private ActivityCategoryBinding binding;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String category = getIntent().getStringExtra("category");
        TextView tvCategory = findViewById(R.id.tvCategory);
        tvCategory.setText(category);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 카테고리별 데이터 불러오기
        productList = getProductsByCategory(category);
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 테스트용 하드 코딩 데이터
    private List<Product> getProductsByCategory(String category) {
        List<Product> list = new ArrayList<>();
        switch (category) {
            case "가정용품":
                list.add(new Product("티셔츠", 15000));
                list.add(new Product("청바지", 30000));
                break;
            case "전자제품":
                list.add(new Product("노트북", 1200000));
                list.add(new Product("스마트폰", 900000));
                break;
            case "식품":
                list.add(new Product("사과", 3000));
                list.add(new Product("우유", 2000));
                break;
        }
        return list;
    }
}

/*
// CategoryActivity.java
public class CategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        String category = getIntent().getStringExtra("category");
        TextView tvCategory = findViewById(R.id.tvCategory);
        tvCategory.setText(category);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 카테고리별 데이터 불러오기
        productList = getProductsByCategory(category);

        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);
    }
}*/
