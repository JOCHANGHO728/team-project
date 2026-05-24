package com.example.customerapp.Customer.Shoppingbasket;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customerapp.BuildConfig;
import com.example.customerapp.databinding.ActivityPaymentBinding;

public class Payment extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private String selectedSimplePay = "토스페이";
    private String selectedPgCode = BuildConfig.PORTONE_PG_TOSS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int totalPrice = getIntent().getIntExtra("total_price", 0);
        if (totalPrice <= 0) {
            Toast.makeText(this, "결제할 상품이 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        binding.tvTotalPaymentPrice.setText("₩ " + totalPrice);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.rbSimple.setOnClickListener(v -> setSimplePaymentSelected(true));
        binding.rbOnSite.setOnClickListener(v -> setSimplePaymentSelected(false));

        binding.btnPayToss.setOnClickListener(v -> selectSimplePay("토스페이"));
        binding.btnPayKakao.setOnClickListener(v -> selectSimplePay("카카오페이"));
        binding.btnPayPayco.setOnClickListener(v -> selectSimplePay("페이코"));

        binding.btnPayFinal.setOnClickListener(v -> {
            if (binding.rbSimple.isChecked()) {
                Intent intent = new Intent(Payment.this, PortOnePaymentWebViewActivity.class);
                intent.putExtra("total_price", totalPrice);
                intent.putExtra("pg_code", selectedPgCode);
                intent.putExtra("pay_name", selectedSimplePay);
                startActivity(intent);
            } else {
                Toast.makeText(Payment.this, "현장결제를 선택했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void selectSimplePay(String payName) {
        selectedSimplePay = payName;
        resetSimplePayButtons();

        if ("토스페이".equals(payName)) {
            selectedPgCode = BuildConfig.PORTONE_PG_TOSS;
            binding.btnPayToss.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
        } else if ("카카오페이".equals(payName)) {
            selectedPgCode = BuildConfig.PORTONE_PG_KAKAO;
            binding.btnPayKakao.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
        } else if ("페이코".equals(payName)) {
            selectedPgCode = BuildConfig.PORTONE_PG_PAYCO;
            binding.btnPayPayco.setStrokeColor(ColorStateList.valueOf(Color.BLACK));
        }
    }

    private void resetSimplePayButtons() {
        int defaultColor = Color.parseColor("#DDDDDD");
        binding.btnPayToss.setStrokeColor(ColorStateList.valueOf(defaultColor));
        binding.btnPayKakao.setStrokeColor(ColorStateList.valueOf(defaultColor));
        binding.btnPayPayco.setStrokeColor(ColorStateList.valueOf(defaultColor));
    }

    private void setSimplePaymentSelected(boolean selected) {
        binding.rbSimple.setChecked(selected);
        binding.rbOnSite.setChecked(!selected);
        binding.gridSimplePay.setVisibility(selected ? android.view.View.VISIBLE : android.view.View.GONE);
    }
}
