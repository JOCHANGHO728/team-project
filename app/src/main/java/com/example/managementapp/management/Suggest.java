package com.example.managementapp.management;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.managementapp.R;
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

    public static class UnregistedProduct extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_unregisted_product);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
}
