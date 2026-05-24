package com.example.customerapp.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customerapp.Customer.Household_Ledger.household_Ledger;
import com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket;
import com.example.customerapp.DataModel.CartManager;
import com.example.customerapp.Login;
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityMyInfoBinding;

public class MyInfo extends AppCompatActivity {
    private ActivityMyInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String userId = CartManager.getInstance().getLoggedInUserId();
        if (userId == null || userId.isBlank()) {
            userId = "로그인 사용자";
        }
        binding.tvUserId.setText(userId);
        binding.tvProfileInitial.setText(userId.substring(0, 1).toUpperCase());

        binding.tvMenuBiometric.setOnClickListener(v ->
                Toast.makeText(this, "로그인 화면에서 생체로그인을 설정할 수 있습니다.", Toast.LENGTH_SHORT).show());

        binding.tvMenuPurchase.setOnClickListener(v -> {
            Intent intent = new Intent(this, household_Ledger.class);
            startActivity(intent);
        });

        binding.btnLogout.setOnClickListener(v -> {
            CartManager.getInstance().setLoggedInUserId("");
            CartManager.getInstance().setAccessToken("");
            CartManager.getInstance().clearCart();
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 1. 네비게이션 바 초기 선택 상태 설정 (내 정보 탭 활성화)
        binding.bottomNavigation.setSelectedItemId(R.id.nav_my_info);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            // 1. 현재 화면(내 정보) 버튼을 눌렀을 때
            if (id == R.id.nav_my_info) {
                return true; // 아무것도 안 하고 현재 화면 유지
            }

            // 2. 다른 화면으로 이동할 때 (instanceof 체크 삭제)
            if (id == R.id.nav_shopping) {
                intent = new Intent(this, com.example.customerapp.Customer.Customer.class);
            }
            else if (id == R.id.nav_cart) {
                intent = new Intent(this, ShoppingBasket.class);
            }
            else if (id == R.id.nav_ledger) {
                intent = new Intent(this, household_Ledger.class);
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
}
