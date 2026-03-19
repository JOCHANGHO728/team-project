package com.example.managementapp.management.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivityDetailSearchBinding;
import com.example.managementapp.model.ProductResponse;

public class DetailSearch extends AppCompatActivity {
    private ActivityDetailSearchBinding binding;
    private ProductResponse product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔥 1) 전달받은 ProductResponse 객체 가져오기
        product = (ProductResponse) getIntent().getSerializableExtra("product");

        if (product != null) {
            binding.name.setText(product.getP_name());
            binding.price.setText(String.valueOf(product.getP_price()));
            binding.quantity.setText(String.valueOf(product.getP_quantity()));
            binding.category.setText(product.getCategory());
            binding.bkey.setText(product.getB_key());
        }

        // 🔥 2) 검색 화면으로 돌아가기
        binding.back.setOnClickListener(v -> {
            Intent intent = new Intent(DetailSearch.this, Search.class);
            startActivity(intent);
        });

        // 🔥 3) 수정 화면으로 이동하기
        binding.update.setOnClickListener(v -> {
            /*Intent intent = new Intent(DetailSearch.this, UpdateProductActivity.class);
            intent.putExtra("product", product); // 수정 화면으로 상품 정보 전달
            startActivity(intent);*/
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}