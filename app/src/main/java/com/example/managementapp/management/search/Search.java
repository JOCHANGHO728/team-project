package com.example.managementapp.management.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivitySearchBinding;
import com.example.managementapp.management.Management;
import com.example.managementapp.model.ApiResponse;
import com.example.managementapp.model.ApiService;
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
    private ProductResponse selectedProduct;

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

        // 어댑터 클릭 리스너 추가
        adapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(Search.this, DetailSearch.class);
            intent.putExtra("product", product);   // Serializable 필요
            startActivity(intent);
        });

        // 상품조회 기능
        binding.search.setOnClickListener(v -> {
            String id = binding.productID.getText().toString();

            // 🔥 ApiResponse<List<ProductResponse>> 로 받도록 수정
            searchApi.search(id).enqueue(new Callback<ApiResponse<List<ProductResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ProductResponse>>> call,
                                       Response<ApiResponse<List<ProductResponse>>> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        ApiResponse<List<ProductResponse>> apiResponse = response.body();

                        if (apiResponse.isSuccess()) {
                            // 🔥 실제 상품 리스트는 apiResponse.data 에 있음
                            List<ProductResponse> products = apiResponse.getData();

                            Log.d("Connect_SUCCESS", "상품 개수: " + products.size());

                            for (int i = 0; i < products.size(); i++) {
                                ProductResponse product = products.get(i);
                                Log.d("Product_List", "index=" + i + ", data=" + product.toString());
                            }

                            // 🔥 RecyclerView에 데이터 넣기
                            adapter.setProductList(products);
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.w("Connect_WARNING", "서버 실패 메시지: " + apiResponse.getMessage());
                        }

                    } else {
                        Log.w("Connect_WARNING", "응답은 성공했지만 body가 없음");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ProductResponse>>> call, Throwable t) {
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