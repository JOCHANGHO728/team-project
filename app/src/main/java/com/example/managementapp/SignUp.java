package com.example.managementapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.managementapp.databinding.ActivitySignUpBinding;
import com.example.managementapp.model.ApiService;
import com.example.managementapp.model.SignUpRequest;
import com.example.managementapp.model.SignUpResponse;

public class SignUp extends AppCompatActivity {
    //Retrofit 객체 생성
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://yourserver.com/api/") // 서버 주소
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ApiService api = retrofit.create(ApiService.class);
    private MyDBHelper dbHelper;
    private TextView textViewMembers;

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbHelper = new MyDBHelper(this);

        // 디버그용 코드
        SQLiteDatabase test = dbHelper.getReadableDatabase();
        Cursor cursor = test.rawQuery("SELECT * FROM members", null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            int password = cursor.getInt(cursor.getColumnIndexOrThrow("password"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int number = cursor.getInt(cursor.getColumnIndexOrThrow("number"));

            Log.d("DB_RESULT", "ID: " + id + ", "+ "password: " + password + ", " + "Name: " + name + ", PhoneNumber: " + number);
        }

        cursor.close();
        test.close();

        // 회원가입 기능
        // 외부 db와 연동 필요함
        binding.signup.setOnClickListener(v -> {
            String id = binding.loginID.getText().toString();
            String password = binding.password.getText().toString();
            String name = binding.name.getText().toString();
            String number = binding.phonenumber.getText().toString();
            SignUpRequest request = new SignUpRequest(id, password, name, number);

            // 데이터베이스 연동 확인용 코드(내부 데이터베이스)
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("password", password);
            values.put("name", name);
            values.put("number", number);

            long result = db.insert("members", null, values);
            if (result != -1) {
                Toast.makeText(this, "회원 정보 저장 성공!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
            }

            

            // 서버에 retrofit 객체 전달
            /*api.register(request).enqueue(new retrofit2.Callback<SignUpResponse>() {
                @Override
                public void onResponse(Call<SignUpResponse> call, retrofit2.Response<SignUpResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SignUpResponse signupResponse = response.body();
                        if (signupResponse.isSuccess()) {
                            Toast.makeText(SignUp.this, "회원가입 성공: " + signupResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUp.this, "회원가입 실패: " + signupResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    Toast.makeText(SignUp.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                }
            });*/
        });

        // 메인화면으로 이동
        binding.out.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}