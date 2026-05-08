package com.example.customerapp.Customer.Shoppingbasket;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.customerapp.databinding.ActivityPaymentBinding;
import java.text.DecimalFormat;

public class Payment extends AppCompatActivity {
    private ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. 뒤로가기 버튼 기능 (바코드 스캔 화면과 동일한 동작)
        binding.btnBack.setOnClickListener(v -> finish());

        // 2. 라디오 버튼 체크 색상 검정색으로 설정
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked }, // 체크 시
                new int[] { -android.R.attr.state_checked } // 해제 시
        };

        int[] colors = new int[] {
                Color.BLACK,
                Color.parseColor("#888888") // 해제 시 회색
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);
        binding.rbSimple.setButtonTintList(colorStateList);
        binding.rbOnSite.setButtonTintList(colorStateList);

        // 3. 결제 금액 수신 및 표시
        int totalPrice = getIntent().getIntExtra("total_price", 0);
        DecimalFormat df = new DecimalFormat("#,###");
        binding.tvTotalPaymentPrice.setText("₩ " + df.format(totalPrice));

        // 4. 결제 완료 버튼 클릭 시
        binding.btnPayFinal.setOnClickListener(v -> {
            String method = binding.rbSimple.isChecked() ? "간편결제" : "현장결제";
            Toast.makeText(this, method + "로 결제를 진행합니다.", Toast.LENGTH_SHORT).show();
            // 이후 실제 결제 로직 추가 가능
        });
    }
}