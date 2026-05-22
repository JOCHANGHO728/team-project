package com.example.customerapp.Customer.Shoppingbasket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customerapp.Customer.BarcodeScan.BarcodeScan;
import com.example.customerapp.Customer.Customer;
import com.example.customerapp.Customer.Household_Ledger.household_Ledger;
import com.example.customerapp.Customer.MyInfo;
import com.example.customerapp.DataModel.ApiResponse;
import com.example.customerapp.DataModel.ApiService;
import com.example.customerapp.DataModel.Product;
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityShoppingBasketBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ShoppingBasket extends AppCompatActivity {

    private static final String BASE_URL = "https://server-jc54.onrender.com/";
    private ActivityShoppingBasketBinding binding;

    private ActivityResultLauncher<Intent> barcodeLauncher;
    private CartAdapter cartAdapter;

    // 🔥 장바구니를 유지하는 전역 리스트
    private List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShoppingBasketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        // RecyclerView
        cartAdapter = new CartAdapter();
        cartAdapter.setOnCartChangeListener(() -> updateTotalPrice());
        binding.rvCartList.setAdapter(cartAdapter);

        // 🔥 스캔 결과 런처
        barcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {

                            HashMap<String, Integer> scannedItems =
                                    (HashMap<String, Integer>) data.getSerializableExtra("scannedItems");

                            if (scannedItems != null && !scannedItems.isEmpty()) {

                                Log.d("SHOPPING_BASKET", "받은 바코드 목록: " + scannedItems.toString());

                                // 🔥 새로 스캔한 바코드들 처리
                                for (String barcode : scannedItems.keySet()) {

                                    int qty = scannedItems.get(barcode);

                                    api.searchByBarcode(barcode).enqueue(new Callback<ApiResponse<Product>>() {
                                        @Override
                                        public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                                            if (response.isSuccessful() && response.body() != null) {

                                                Product product = response.body().getData();

                                                // 🔥 product가 null이면 DB에 없는 상품
                                                if (product == null) {
                                                    Toast.makeText(
                                                            ShoppingBasket.this,
                                                            "등록되지 않은 상품입니다: " + barcode,
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                    return;
                                                }

                                                // 🔥 기존 장바구니에 같은 상품이 있는지 확인
                                                boolean exists = false;
                                                for (CartItem item : cartItems) {
                                                    if (item.getProduct().getPId().equals(product.getPId())) {
                                                        // 이미 있는 상품이면 수량만 증가
                                                        item.setQuantity(item.getQuantity() + qty);
                                                        exists = true;
                                                        break;
                                                    }
                                                }

                                                // 🔥 장바구니에 없는 상품이면 새로 추가
                                                if (!exists) {
                                                    product.setCartQuantity(qty);
                                                    cartItems.add(new CartItem(product, qty));
                                                }

                                                // UI 업데이트
                                                cartAdapter.setData(cartItems);
                                                updateTotalPrice();

                                            } else {
                                                Toast.makeText(
                                                        ShoppingBasket.this,
                                                        "등록되지 않은 상품입니다: " + barcode,
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                                            Log.e("SERVER", "서버 오류: " + t.getMessage());
                                        }
                                    });
                                }

                            } else {
                                Log.d("SHOPPING_BASKET", "스캔된 바코드 없음");
                            }
                        }
                    }
                }
        );

        // 스캔 버튼
        binding.btnBarcodeScan.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingBasket.this, BarcodeScan.class);
            barcodeLauncher.launch(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 하단 네비게이션 바 설정
        binding.bottomNavigation.setSelectedItemId(R.id.nav_ledger);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_ledger) {
                intent = new Intent(this, household_Ledger.class);
            } else if (id == R.id.nav_shopping) {
                return true;
            } else if (id == R.id.nav_cart) {
                intent = new Intent(this, ShoppingBasket.class);
            } else if (id == R.id.nav_my_info) {
                intent = new Intent(this, MyInfo.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateTotalPrice() {
        int total = 0;

        for (CartItem item : cartItems) {
            int price = item.getProduct().getPPrice();  // 상품 가격
            int qty = item.getQuantity();               // 수량
            total += price * qty;
        }

        binding.tvTotalPrice.setText("총 금액: " + total + "원");
        binding.tvScannedPrice.setText("₩ " + total);
    }

}