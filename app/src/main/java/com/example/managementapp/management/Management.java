package com.example.managementapp.management;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.managementapp.MainActivity;
import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivityManagementBinding;
import com.example.managementapp.management.search.Search;

public class Management extends AppCompatActivity {
    private ActivityManagementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 데이터베이스 조회
        binding.search.setOnClickListener(v -> {
            Intent intent = new Intent(Management.this, Search.class);
            startActivity(intent);
        });

        // 데이터베이스 수정 및 삭제
        binding.change.setOnClickListener(v -> {
            Intent intent = new Intent(Management.this, Change.class);
            startActivity(intent);
        });

        // 데이터 베이스 추천(ai 기능)
        binding.suggest.setOnClickListener(v -> {
            Intent intent = new Intent(Management.this, Change.class);
            startActivity(intent);
        });

        // 로그아웃
        binding.logout.setOnClickListener(v -> {
            Intent intent = new Intent(Management.this, MainActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}