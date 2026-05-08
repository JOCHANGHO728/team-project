package com.example.managementapp.management.search;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.managementapp.databinding.ActivityChangeBinding;
import com.example.managementapp.management.ProductDB;
import com.example.managementapp.model.ApiResponse;
import com.example.managementapp.model.ApiService;
import com.example.managementapp.model.ProductResponse;
import com.example.managementapp.model.ProductUpdateRequest;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Update extends AppCompatActivity {
    private static final String BASE_URL = "https://server-jc54.onrender.com";

    private ActivityChangeBinding binding;
    private ProductResponse product;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        product = (ProductResponse) getIntent().getSerializableExtra("product");

        if (product != null) {
            binding.tvDisplayName.setText(product.getP_name());
            binding.tvDisplayBarcode.setText(product.getB_key());
            binding.editPrice.setText(String.valueOf(product.getP_price()));
            binding.changeAmount.setText(String.valueOf(product.getP_quantity()));
        }

        binding.change.setOnClickListener(v -> saveUpdatedData());
        binding.out.setOnClickListener(v -> finish());
    }

    private void saveUpdatedData() {
        if (product == null) {
            Toast.makeText(this, "상품 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String priceStr = binding.editPrice.getText().toString().trim();
        String quantityStr = binding.changeAmount.getText().toString().trim();

        if (priceStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "가격/수량을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int newPrice;
        int newQuantity;
        try {
            newPrice = Integer.parseInt(priceStr);
            newQuantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "가격/수량은 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductUpdateRequest request = new ProductUpdateRequest(newPrice, newQuantity);
        Log.d("UPDATE_REQ", "pId=" + product.getP_id() + ", body=" + new Gson().toJson(request));

        apiService.updateProduct(product.getP_id(), request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                Log.d("UPDATE_RES", "code=" + response.code() + ", body=" + new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ProductDB productDB = new ProductDB(Update.this);
                    SQLiteDatabase db = productDB.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("name", binding.tvDisplayName.getText().toString().trim());
                    values.put("price", newPrice);
                    values.put("quantity", newQuantity);
                    int updated = db.update("products", values, "b_key = ?", new String[]{product.getB_key()});
                    db.close();

                    Toast.makeText(Update.this, "상품 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Update.this, Search.class);
                    intent.putExtra("localUpdated", updated > 0);
                    startActivity(intent);
                    finish();
                } else {
                    String message = "수정 실패: " + response.code();
                    if (response.body() != null && response.body().getMessage() != null) {
                        message = "수정 실패: " + response.body().getMessage();
                    }
                    Toast.makeText(Update.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(Update.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
