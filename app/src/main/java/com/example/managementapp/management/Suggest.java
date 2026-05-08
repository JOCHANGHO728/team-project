package com.example.managementapp.management;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.managementapp.databinding.ActivitySuggestBinding;

public class Suggest extends AppCompatActivity {
    private ActivitySuggestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 스크롤로 내역 보여주기?
        super.onCreate(savedInstanceState);
        binding = ActivitySuggestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 하단 버튼 작동
        // binding.bottomButton.setOnClickListener(v -> );
    }

    // 이 밑으로 뭔가를 보여줘야 되는데 뭔 내용을 넣어야 할 지 감이 안 와서 일단 공란으로 둠
}
