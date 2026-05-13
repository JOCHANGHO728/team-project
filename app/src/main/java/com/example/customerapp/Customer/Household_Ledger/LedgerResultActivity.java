package com.example.customerapp.Customer.Household_Ledger;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.customerapp.databinding.ActivityLedgerResultBinding;

public class LedgerResultActivity extends AppCompatActivity {
    private ActivityLedgerResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLedgerResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 날짜 데이터 수신 (필요한 경우 로직에서 사용 가능하나 표시 코드는 삭제)
        String startDate = getIntent().getStringExtra("start_date");
        String endDate = getIntent().getStringExtra("end_date");

        // 상단 뒤로가기 화살표 버튼 기능
        binding.btnBackArrow.setOnClickListener(v -> {
            finish();
        });

        // 하단 확인 버튼 기능
        binding.btnClose.setOnClickListener(v -> {
            finish();
        });
    }
}