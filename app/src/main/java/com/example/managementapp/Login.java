package com.example.managementapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.managementapp.databinding.ActivityLoginBinding;
import com.example.managementapp.management.Management;
import com.example.managementapp.model.ApiService;
import com.example.managementapp.model.LoginRequest;
import com.example.managementapp.model.LoginResponse;

public class Login extends AppCompatActivity {
    // 서버 주소
    private static final String BASE_URL = "https://server-jc54.onrender.com/";
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService loginapi = retrofit.create(ApiService.class);

        binding.out.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });

        // 로그인 기능
        binding.login.setOnClickListener(v -> {
            String id = binding.loginID.getText().toString();
            String password = binding.password.getText().toString();
            LoginRequest request = new LoginRequest(id, password);
            Log.d("LOGIN_Try", "로그인 시도");

            loginapi.login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse body = response.body();
                        if (body.isSuccess()) {
                            Toast.makeText(Login.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, Management.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, body.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.w("Login error", body.getMessage());
                        }
                    } else {
                        Toast.makeText(Login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        Log.w("Login error", "HTTP " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("Connect_WARNING", "서버 연결 실패: " + t.getMessage());
                }
            });

        });

        // 회원가입 화면으로 이동(SignUp)
        /*binding.signup.setOnClickListener(v ->{
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });*/

        // 지문 인식 로그인
        binding.fingerprintLogin.setOnClickListener(v ->{
            Intent intent = new Intent(Login.this, FingerprintActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
