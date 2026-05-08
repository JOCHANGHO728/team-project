package com.example.customerapp.Customer;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket;
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityMyInfoBinding;

public class MyInfo extends AppCompatActivity {
    private ActivityMyInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                intent = new Intent(this, com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket.class);
            }
            else if (id == R.id.nav_ledger) {
                intent = new Intent(this, com.example.customerapp.Customer.Household_Ledger.household_Ledger.class);
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