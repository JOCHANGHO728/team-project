package com.example.managementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.managementapp.databinding.ActivitySignUpBinding;

public class SignUp extends AppCompatActivity {
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signup.setOnClickListener(v -> {
            String id = binding.loginID.getText().toString().trim();
            String password = binding.password.getText().toString().trim();
            String name = binding.name.getText().toString().trim();
            String phoneNumber = binding.phonenumber.getText().toString().trim();

            if (id.isEmpty() || password.isEmpty() || name.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "회원가입 기능은 서버 API 연결 후 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
        });

        binding.out.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
