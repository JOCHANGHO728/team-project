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
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityShoppingBasketBinding;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
// 이동할 화면들 (패키지 경로 확인 필요)
import com.example.customerapp.Customer.Customer;
import com.example.customerapp.Customer.Household_Ledger.household_Ledger;
import com.example.customerapp.Customer.MyInfo;

public class ShoppingBasket extends AppCompatActivity {
    private static final String BASE_URL = "https://server-jc54.onrender.com/";
    private ActivityShoppingBasketBinding binding;

    private ActivityResultLauncher<Intent> barcodeLauncher;
    private CartAdapter cartAdapter;

    private int totalAmount = 0; // 결제할 총 금액

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShoppingBasketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // recyclerView 초기화 (XML의 rv_cart_list와 연결)
        cartAdapter = new CartAdapter();
        binding.rvCartList.setAdapter(cartAdapter); // 이 부분 ID를 맞췄습니다!

        // 런처 초기화
        barcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        HashMap<String, Integer> scannedItems =
                                (HashMap<String, Integer>) result.getData().getSerializableExtra("scannedItems");

                        if (scannedItems != null && !scannedItems.isEmpty()) {
                            cartAdapter.setData(scannedItems);
                            // 스캔 결과가 오면 금액 계산 업데이트
                            updateTotalPrice(scannedItems);
                        }
                    }
                }
        );

        // 바코드 스캔 버튼
        binding.btnBarcodeScan.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingBasket.this, BarcodeScan.class);
            barcodeLauncher.launch(intent);
        });

        // 주문하기 버튼 (Payment 액티비티로 금액 전달)
        binding.btnOrder.setOnClickListener(v -> {
            if (totalAmount > 0) {
                Intent intent = new Intent(ShoppingBasket.this, Payment.class);
                intent.putExtra("total_price", totalAmount);
                startActivity(intent);
            } else {
                Toast.makeText(this, "장바구니가 비어있습니다.", Toast.LENGTH_SHORT).show();
            }
        });
// --- 하단 네비게이션바 설정 추가 ---
        // 1. 현재 탭을 '장바구니'로 표시
        binding.bottomNavigation.setSelectedItemId(R.id.nav_cart);

        // 2. 메뉴 클릭 시 화면 이동 설정
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_cart) {
                return true; // 현재 화면이므로 아무것도 안 함
            } else if (id == R.id.nav_shopping) {
                intent = new Intent(this, Customer.class);
            } else if (id == R.id.nav_ledger) {
                intent = new Intent(this, household_Ledger.class);
            } else if (id == R.id.nav_my_info) {
                intent = new Intent(this, MyInfo.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0); // 화면 전환 애니메이션 제거
                finish(); // 현재 화면 종료 (스택 관리)
                return true;
            }
            return false;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    // 금액 계산 및 텍스트뷰 업데이트
    private void updateTotalPrice(Map<String, Integer> items) {
        totalAmount = 0;
        for (int count : items.values()) {
            totalAmount += (1500 * count); // 개당 1500원 가정
        }
        DecimalFormat df = new DecimalFormat("#,###");
        binding.tvScannedPrice.setText("₩ " + df.format(totalAmount));
        binding.tvTotalPrice.setText("₩ " + df.format(totalAmount));
    }
}