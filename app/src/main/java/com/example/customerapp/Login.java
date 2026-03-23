package com.example.customerapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customerapp.DataModel.LoginRequest;
import com.example.customerapp.DataModel.SignUpRequest;
import com.example.customerapp.databinding.ActivityLoginBinding;
import com.example.customerapp.databinding.ActivitySignUpBinding;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Login extends AppCompatActivity {
    private static final String BASE_URL = "https://server-jc54.onrender.com/";
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 로그인 기능
        binding.btnLoginSubmit.setOnClickListener(v -> {
            String uId = binding.etLoginId.getText().toString();
            String uPassword = binding.etLoginPw.getText().toString();

            // 입력값 검증
            if (uId.isEmpty()) {
                Toast.makeText(Login.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (uPassword.isEmpty()) {
                Toast.makeText(Login.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 모든 값이 정상적으로 입력된 경우, 요청 객체 생성
            LoginRequest request = new LoginRequest(uId, uPassword);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}