package com.example.customerapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customerapp.DataModel.SignUpRequest;
import com.example.customerapp.databinding.ActivitySignUpBinding;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SignUp extends AppCompatActivity {
    private static final String BASE_URL = "https://server-jc54.onrender.com/";
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 회원가입 정보 제출
        binding.btnSignupSubmit.setOnClickListener(v -> {
            String uId = binding.etSignupId.getText().toString();
            String uPassword = binding.etSignupPw.getText().toString();
            String uName = binding.etSignupName.getText().toString();
            String uNum = binding.etSignupPhone.getText().toString();

            // 입력값 검증
            if (uId.isEmpty()) {
                Toast.makeText(SignUp.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (uPassword.isEmpty()) {
                Toast.makeText(SignUp.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (uName.isEmpty()) {
                Toast.makeText(SignUp.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (uNum.isEmpty()) {
                Toast.makeText(SignUp.this, "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 모든 값이 정상적으로 입력된 경우, 요청 객체 생성
            SignUpRequest request = new SignUpRequest(uId, uPassword, uName, uNum);


            /*회원가입 성공시 로그인 화면으로 이동하는 흐름으로 설계 예정
                    실패시 사용자에게 Toast 메시지 출력*/
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}