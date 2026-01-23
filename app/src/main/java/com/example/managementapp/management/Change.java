package com.example.managementapp.management;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivityChangeBinding;
import com.example.managementapp.databinding.ActivityManagementBinding;

public class Change extends AppCompatActivity {
    private ActivityChangeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //검색 기능
        binding.search.setOnClickListener(v -> {

        });

        //수량 변경 기능
        binding.change.setOnClickListener(v -> {

        });

        //관리 화면으로 돌아가기
        binding.out.setOnClickListener(v -> {

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}