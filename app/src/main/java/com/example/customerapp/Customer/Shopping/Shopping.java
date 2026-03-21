package com.example.customerapp.Customer.Shopping;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customerapp.MainActivity;
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityShoppingBinding;

public class Shopping extends AppCompatActivity {
    private ActivityShoppingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShoppingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 카테고리 이벤트 리스너
        binding.snack.setOnClickListener(v -> openCategory("과자"));
        binding.drink.setOnClickListener(v -> openCategory("음료"));
        binding.noodle.setOnClickListener(v -> openCategory("면류"));
        binding.dairy.setOnClickListener(v -> openCategory("유제품"));
        binding.instant.setOnClickListener(v -> openCategory("즉석 조리"));
        binding.fresh.setOnClickListener(v -> openCategory("신선식품"));
        binding.frozen.setOnClickListener(v -> openCategory("냉동식품"));
        binding.household.setOnClickListener(v -> openCategory("가정용품"));
        binding.hygiene.setOnClickListener(v -> openCategory("위생용품"));

        // 장바구니 조회
        binding.search.setOnClickListener(v -> {

        });

        // 장바구니 초기화
        binding.reset.setOnClickListener(v -> {

        });

        // 메인 페이지로 돌아가기
        binding.out.setOnClickListener(v -> {
            Intent intent = new Intent(Shopping.this, MainActivity.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 이벤트리스너 함수
    private void openCategory(String category) {
        Intent intent = new Intent(Shopping.this, Category.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}