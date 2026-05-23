package com.example.customerapp.Customer.Shoppingbasket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customerapp.databinding.ActivityPaymentBinding;

public class Payment extends AppCompatActivity {
    private ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int totalPrice = getIntent().getIntExtra("total_price", 0);
        binding.tvTotalPaymentPrice.setText("₩ " + totalPrice);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnPayFinal.setOnClickListener(v -> {
            if (binding.rbSimple.isChecked()) {
                Intent intent = new Intent(Payment.this, PaymentWebViewActivity.class);
                intent.putExtra("total_price", totalPrice);
                startActivity(intent);
            } else {
                Toast.makeText(Payment.this, "현장결제를 선택했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
