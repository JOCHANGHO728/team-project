package com.example.managementapp.management;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivityChangeBinding;
import com.example.managementapp.databinding.ActivityManagementBinding;
import com.example.managementapp.model.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Change extends AppCompatActivity {
    // 서버 주소
    private static final String BASE_URL = "https://server-jc54.onrender.com/";
    private ActivityChangeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrofit 객체 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService changeApi = retrofit.create(ApiService.class);

        //검색 기능
        binding.change.setOnClickListener(v -> {
        });

        //수량 변경 기능
        binding.change.setOnClickListener(v -> {
        });

        //관리 화면으로 돌아가기
        binding.out.setOnClickListener(v -> {
            Intent intent = new Intent(Change.this, Management.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
