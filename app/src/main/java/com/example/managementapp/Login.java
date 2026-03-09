package com.example.managementapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

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
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService loginapi = retrofit.create(ApiService.class);

        binding.out.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });

        // 회원가입 화면으로 이동(SignUp)
        /*binding.signup.setOnClickListener(v ->{
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });*/

        // 로그인 기능
        binding.login.setOnClickListener(v -> {
            String id = binding.loginID.getText().toString();
            String password = binding.password.getText().toString();
            LoginRequest request = new LoginRequest(id, password);
            Log.d("LOGIN_Try", "로그인 시도");

            loginapi.login(request).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String message = response.body();
                        Toast.makeText(Login.this, "로그인 성공: " + message, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Login.this, Management.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        Log.w("Login error", "로그인 정보 오류");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Connect_WARNING", "서버 연결 실패" + t.getMessage());
                }
            });

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}