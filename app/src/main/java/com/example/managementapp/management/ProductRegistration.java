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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductRegistration extends AppCompatActivity {
    private static final String BASE_URL = "https://server-jc54.onrender.com/";
    private static final String TAG = "ProductRegister";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private ActivityProductRegistarationBinding binding;
    private ApiService apiService;

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
        apiService = retrofit.create(ApiService.class);

        binding.btnRegister.setOnClickListener(v -> registerProduct());

        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProductRegistration.this, Management.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void registerProduct() {
        String barcode = binding.editBarcode.getText().toString().trim();
        String name = binding.editName.getText().toString().trim();
        String priceText = binding.editPrice.getText().toString().trim();
        String amountText = binding.editAmount.getText().toString().trim();
        String category = binding.editCategory.getText().toString().trim();

        if (barcode.isEmpty()) {
            showMessage("바코드를 입력하세요.");
            return;
        }

        if (name.isEmpty()) {
            showMessage("상품명을 입력하세요.");
            return;
        }

        if (priceText.isEmpty()) {
            showMessage("가격을 입력하세요.");
            return;
        }

        if (amountText.isEmpty()) {
            showMessage("수량을 입력하세요.");
            return;
        }

        if (category.isEmpty()) {
            showMessage("카테고리를 입력하세요.");
            return;
        }

        int price;
        int quantity;
        try {
            price = Integer.parseInt(priceText);
            quantity = Integer.parseInt(amountText);
        } catch (NumberFormatException e) {
            showMessage("가격과 수량은 숫자로 입력하세요.");
            return;
        }

        RequestBody request;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pName", name);
            jsonObject.put("pPrice", price);
            jsonObject.put("pQuantity", quantity);
            jsonObject.put("bKey", barcode);
            jsonObject.put("category", category);

            String requestJson = jsonObject.toString();
            Log.e(TAG, "request json=" + requestJson);
            request = RequestBody.create(JSON_MEDIA_TYPE, requestJson);
        } catch (JSONException e) {
            showMessage("상품 정보를 만들 수 없습니다.");
            Log.e(TAG, "failed to create product json", e);
            return;
        }

        String token = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("managerToken", "");

        if (token.isEmpty()) {
            showMessage("로그인이 필요합니다. 다시 로그인하세요.");
            return;
        }

        apiService.addProduct("Bearer " + token, request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {
                    showMessage("상품이 등록되었습니다.");
                    clearInputs();
                    return;
                }

                String message = getErrorMessage(response);
                showMessage(message);
                Log.e(TAG, message);
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showMessage("서버 통신 오류: " + t.getMessage());
                Log.e(TAG, "server error", t);
            }
        });
    }

    private String getErrorMessage(Response<ApiResponse<Void>> response) {
        if (response.body() != null && response.body().getMessage() != null) {
            return response.body().getMessage();
        }

        if (response.errorBody() != null) {
            try {
                String errorJson = response.errorBody().string();
                Log.e(TAG, "error body=" + errorJson);
                return "상품 등록 실패: " + response.code();
            } catch (IOException e) {
                Log.e(TAG, "failed to read error body", e);
            }
        }

        return "상품 등록 실패: " + response.code();
    }

    private void clearInputs() {
        binding.editBarcode.setText("");
        binding.editName.setText("");
        binding.editPrice.setText("");
        binding.editAmount.setText("");
        binding.editCategory.setText("");
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
