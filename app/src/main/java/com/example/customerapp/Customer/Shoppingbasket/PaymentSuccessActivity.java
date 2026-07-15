package com.example.customerapp.Customer.Shoppingbasket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customerapp.DataModel.ApiResponse;
import com.example.customerapp.DataModel.CartManager;
import com.example.customerapp.DataModel.OrderItem;
import com.example.customerapp.DataModel.OrderRequest;
import com.example.customerapp.DataModel.Product;
import com.example.customerapp.DataModel.RetrofitClient;
import com.example.customerapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentSuccessActivity extends AppCompatActivity {
    private Button btnReturn;
    private boolean orderSaved = false;
    private boolean orderSaving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        int totalPrice = getIntent().getIntExtra("total_price", 0);
        String merchantUid = getIntent().getStringExtra("merchant_uid");
        String paymentType = getIntent().getStringExtra("payment_type");

        TextView tvAmount = findViewById(R.id.tv_success_amount);
        TextView tvOrderId = findViewById(R.id.tv_success_order_id);
        TextView tvReceipt = findViewById(R.id.tv_success_receipt);
        btnReturn = findViewById(R.id.btn_success_return);

        tvAmount.setText((paymentType == null ? "결제" : paymentType) + " 금액: ₩ " + totalPrice);
        if (merchantUid == null || merchantUid.isBlank()) {
            tvOrderId.setText("주문번호: 확인 중");
        } else {
            tvOrderId.setText("주문번호: " + merchantUid);
        }
        tvReceipt.setText(buildReceiptText(totalPrice));

        btnReturn.setOnClickListener(v -> {
            if (orderSaved) {
                moveToBasket();
            } else {
                saveOrder();
            }
        });
        saveOrder();
    }

    private String buildReceiptText(int totalPrice) {
        List<Product> cartProducts = CartManager.getInstance().getScannedCartItems();
        if (cartProducts == null || cartProducts.isEmpty()) {
            return "결제 품목이 없습니다.";
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("구매 영수증\n");
        receipt.append("------------------------------\n");

        int calculatedTotal = 0;
        for (Product product : cartProducts) {
            String name = product.getPName();
            if (name == null || name.isBlank()) {
                name = "상품명 없음";
            }

            int quantity = product.getScannedQuantity();
            int lineTotal = product.getPPrice() * quantity;
            calculatedTotal += lineTotal;

            receipt.append(name).append("\n");
            receipt.append(String.format(Locale.KOREA, "  %,d원 x %d개 = %,d원\n",
                    product.getPPrice(), quantity, lineTotal));
        }

        receipt.append("------------------------------\n");
        receipt.append(String.format(Locale.KOREA, "합계: %,d원\n",
                totalPrice > 0 ? totalPrice : calculatedTotal));
        return receipt.toString();
    }

    private void saveOrder() {
        if (orderSaving || orderSaved) {
            return;
        }

        orderSaving = true;
        btnReturn.setEnabled(false);
        btnReturn.setText("결제내역 저장 중...");

        List<Product> cartProducts = CartManager.getInstance().getScannedCartItems();
        if (cartProducts == null || cartProducts.isEmpty()) {
            orderSaved = true;
            orderSaving = false;
            btnReturn.setEnabled(true);
            btnReturn.setText("장바구니로 돌아가기");
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : cartProducts) {
            if (product.getPId() != null && product.getScannedQuantity() > 0) {
                orderItems.add(new OrderItem(product.getPId(), product.getScannedQuantity()));
            }
        }

        if (orderItems.isEmpty()) {
            orderSaved = true;
            orderSaving = false;
            btnReturn.setEnabled(true);
            btnReturn.setText("장바구니로 돌아가기");
            return;
        }

        String loginId = CartManager.getInstance().getLoggedInUserId();
        if (loginId == null || loginId.isBlank()) {
            Toast.makeText(this, "로그인 정보가 없어 주문 저장에 실패했습니다.", Toast.LENGTH_LONG).show();
            orderSaving = false;
            btnReturn.setEnabled(true);
            btnReturn.setText("다시 저장 후 돌아가기");
            return;
        }

        OrderRequest request = new OrderRequest(loginId, orderItems);
        RetrofitClient.getInstance().getApiService().createOrder(request)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            CartManager.getInstance().clearScannedItemsAfterPayment();
                            orderSaved = true;
                            orderSaving = false;
                            btnReturn.setEnabled(true);
                            btnReturn.setText("장바구니로 돌아가기");
                            return;
                        }
                        Toast.makeText(PaymentSuccessActivity.this, "결제내역 저장에 실패했습니다.", Toast.LENGTH_LONG).show();
                        orderSaving = false;
                        btnReturn.setEnabled(true);
                        btnReturn.setText("다시 저장 후 돌아가기");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Toast.makeText(PaymentSuccessActivity.this, "네트워크 오류로 결제내역 저장에 실패했습니다.", Toast.LENGTH_LONG).show();
                        orderSaving = false;
                        btnReturn.setEnabled(true);
                        btnReturn.setText("다시 저장 후 돌아가기");
                    }
                });
    }

    @Override
    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        if (!orderSaved) {
            Toast.makeText(this, "결제내역 저장 후 이동할 수 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        moveToBasket();
    }

    private void moveToBasket() {
        Intent intent = new Intent(PaymentSuccessActivity.this, ShoppingBasket.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
