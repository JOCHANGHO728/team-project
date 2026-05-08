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

    private List<Product> getProductsByCategory(String categoryName) {
        List<Product> filteredList = new ArrayList<>();

        // TODO: 여기에 Firebase나 DB에서 데이터를 가져와서 filteredList에 추가하는 로직을 구현하세요.

        return filteredList;
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
