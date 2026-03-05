package com.example.managementapp.management.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivitySearchBinding;
import com.example.managementapp.management.Management;
import com.example.managementapp.model.ApiService;
import com.example.managementapp.model.ProductRequest;
import com.example.managementapp.model.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Search extends AppCompatActivity {
    // 서버 주소
    private static final String BASE_URL = "https://server-jc54.onrender.com";
    private ActivitySearchBinding binding;
    private ProductAdapter adapter;   // 🔥 추가: 어댑터 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrofit 객체 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService searchApi = retrofit.create(ApiService.class);

        // 🔥 RecyclerView 초기 설정
        adapter = new ProductAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);


        // 상품조회 기능
        binding.search.setOnClickListener(v -> {
            String id = binding.productID.getText().toString();

            searchApi.search(id).enqueue(new Callback<List<ProductResponse>>() {
                @Override
                public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ProductResponse> products = response.body();
                        Log.d("Connect_SUCCESS", "상품 개수: " + products.size());// 전체 리스트 로그 출력
                        for (int i = 0; i < products.size(); i++) {
                            ProductResponse product = products.get(i);
                            Log.d("Product_List", "index=" + i + ", data=" + product.toString());

                            // 🔥 RecyclerView에 데이터 넣기
                            adapter.setProductList(products);
                        }

                    } else {
                        Log.w("Connect_WARNING", "응답은 성공했지만 body가 없음");
                    }
                }

                @Override
                public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                    Log.e("Connect_FAIL", "상품 조회 실패: " + t.getMessage());
                }
            });

        });

        // 관리 화면으로 돌아가기
        binding.out.setOnClickListener(v -> {
            Intent intent = new Intent(Search.this, Management.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


}