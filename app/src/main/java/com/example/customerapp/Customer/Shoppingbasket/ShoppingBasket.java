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
import com.example.customerapp.DataModel.CartManager;
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
        syncCartFromManager();

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

                                                for (int i = 0; i < qty; i++) {
                                                    CartManager.getInstance().addItem(product);
                                                }

                                                syncCartFromManager();

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

        // 결제 버튼
        binding.btnOrder.setOnClickListener(v -> {
            int totalPrice = calculateTotalPrice();
            if (totalPrice <= 0) {
                Toast.makeText(ShoppingBasket.this, "장바구니가 비어있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(ShoppingBasket.this, PortOnePaymentWebViewActivity.class);
            intent.putExtra("total_price", totalPrice);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 하단 네비게이션 바 설정
        binding.bottomNavigation.setSelectedItemId(R.id.nav_cart);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_cart) {
                return true;
            } else if (id == R.id.nav_shopping) {
                intent = new Intent(this, Customer.class);
            } else if (id == R.id.nav_ledger) {
                intent = new Intent(this, household_Ledger.class);
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
        int total = calculateTotalPrice();

        binding.tvTotalPrice.setText("총 금액: " + total + "원");
        binding.tvScannedPrice.setText("₩ " + total);
    }

    private int calculateTotalPrice() {
        int total = 0;
        for (CartItem item : cartItems) {
            int price = item.getProduct().getPPrice();
            int qty = item.getQuantity();
            total += price * qty;
        }
        return total;
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncCartFromManager();
    }

    private void syncCartFromManager() {
        cartItems.clear();
        for (Product product : CartManager.getInstance().getCartItems()) {
            cartItems.add(new CartItem(product, product.getCartQuantity()));
        }
        cartAdapter.setData(cartItems);
        updateTotalPrice();
    }

}
