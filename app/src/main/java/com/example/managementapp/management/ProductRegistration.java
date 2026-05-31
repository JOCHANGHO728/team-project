package com.example.managementapp.management;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivityProductRegistarationBinding;
import com.example.managementapp.model.ApiResponse;
import com.example.managementapp.model.ApiService;
import com.example.managementapp.model.ProductCreateRequest;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ProductRegistration extends AppCompatActivity {
    // 서버 주소
    private static final String BASE_URL = "https://server-jc54.onrender.com/";
    private ActivityProductRegistarationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProductRegistarationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService CreateApi = retrofit.create(ApiService.class);

        // 상품 등록
        binding.btnRegister.setOnClickListener(v -> {
            // EditText 값 가져오기
            String barcode = binding.editBarcode.getText().toString().trim();
            String name = binding.editName.getText().toString().trim();
            String priceText = binding.editPrice.getText().toString().trim();
            String amountText = binding.editAmount.getText().toString().trim();
            String category = binding.editCategory.getText().toString().trim();

            // 숫자 변환
            int price = priceText.isEmpty() ? 0 : Integer.parseInt(priceText);
            int quantity = amountText.isEmpty() ? 0 : Integer.parseInt(amountText);

            // ProductCreateRequest 객체 생성
            ProductCreateRequest request = new ProductCreateRequest(
                    name,       // pName
                    price,      // pPrice
                    quantity,   // pQuantity
                    barcode,    // bKey
                    category    // category
            );

            // 로그로 확인
            Log.d("ProductRegister", "생성된 객체: " + request.toString());
            Log.d("ProductRegister", "바코드 입력값: [" + barcode + "]");

            // 🔥 로그인에서 저장한 토큰 불러오기
            String token = getSharedPreferences("auth", MODE_PRIVATE)
                    .getString("managerToken", null);

            if (token == null) {
                Toast.makeText(this, "토큰이 없습니다. 다시 로그인하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String authHeader = "Bearer " + token;
            // 🔥 서버에 전송
            CreateApi.addProduct(authHeader, request).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, retrofit2.Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful()) {
                        Log.d("ProductRegister", "상품 등록 성공");
                    } else {
                        Log.e("ProductRegister", "상품 등록 실패: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Log.e("ProductRegister", "서버 통신 오류: " + t.getMessage());
                }
            });

        });

        // 관리 메뉴로 돌아가기
        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProductRegistration.this, Management.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}