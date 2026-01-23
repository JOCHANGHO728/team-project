package com.example.managementapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.managementapp.databinding.ActivityLoginBinding;
import com.example.managementapp.model.ApiService;
import com.example.managementapp.model.LoginRequest;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://yourserver.com/api/") // 서버 주소
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ApiService api = retrofit.create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyDBHelper dbHelper = new MyDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //메인 화면으로 이동함
        binding.out.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });

        // 회원가입 화면으로 이동(SignUp)
        binding.signup.setOnClickListener(v ->{
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });

        // 로그인 기능
        binding.login.setOnClickListener(v -> {
            String id = binding.loginID.getText().toString();
            String password = binding.password.getText().toString();
            LoginRequest request = new LoginRequest(id, password);

            Intent intent = new Intent(Login.this, com.example.managementapp.management.Management.class);
            startActivity(intent);
            /*//내부 데이터베이스 테스트 코드
            Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id=? AND password=?",
                    new String[]{id, password});

            if (cursor.moveToFirst()) {
                // 로그인 성공 → SharedPreferences에 상태 저장
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("userId", id);
                editor.apply();

                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "아이디 또는 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            }

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
            if (isLoggedIn) {
                // 로그인된 상태 → 게시물 화면으로 이동
                String userId = prefs.getString("userId", "");
                Toast.makeText(this, userId + "님 로그인 중", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, com.example.managementapp.management.Management.class);
                startActivity(intent);
            } else {
                // 로그인 안 된 상태 → 로그인 화면으로 이동
            }*/


            /*cursor.close();
            db.close();*/


            /*api.login(request).enqueue(new retrofit2.Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse.isSuccess()) {
                            Toast.makeText(Login.this, "로그인 성공: " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "로그인 실패: " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(Login.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                }
            });*/
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}