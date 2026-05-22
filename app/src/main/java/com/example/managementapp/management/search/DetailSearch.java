package com.example.managementapp.management.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivityDetailsearchBinding;
import com.example.managementapp.model.ProductResponse;

public class DetailSearch extends AppCompatActivity {

    private ActivityDetailsearchBinding binding;
    private ProductResponse product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔥 1) 전달받은 ProductResponse 객체 가져오기
        product = (ProductResponse) getIntent().getSerializableExtra("product");

        // 🔥 2) 화면에 데이터 표시
        if (product != null) {
            binding.tvDisplayName.setText(product.getP_name());
            binding.tvDisplayBarcode.setText(product.getB_key());
            binding.tvDisplayPrice.setText(String.valueOf(product.getP_price()));
            binding.tvDisplayQuantity.setText(product.getP_quantity() + "개");
        }

        // 🔥 3) 이전 화면으로 돌아가기
        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(DetailSearch.this, Search.class);
            startActivity(intent);
        });

        // 🔥 4) 수정 화면으로 이동 (추후 구현)
        binding.btnGoToEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DetailSearch.this, Update.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });

        // 시스템 UI 패딩 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
