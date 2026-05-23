package com.example.customerapp.Customer.Household_Ledger;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.customerapp.DataModel.ApiResponse;
import com.example.customerapp.DataModel.CartManager;
import com.example.customerapp.DataModel.PurchaseHistoryItem;
import com.example.customerapp.DataModel.RetrofitClient;
import com.example.customerapp.databinding.ActivityLedgerResultBinding;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LedgerResultActivity extends AppCompatActivity {
    private ActivityLedgerResultBinding binding;
    private LedgerReceiptAdapter receiptAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLedgerResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String startDate = getIntent().getStringExtra("start_date");
        String endDate = getIntent().getStringExtra("end_date");
        String periodText = (startDate != null && endDate != null)
                ? startDate + " ~ " + endDate
                : "-";
        binding.tvSelectedDate.setText(periodText);

        // 상단 뒤로가기 화살표 버튼 기능
        binding.btnBackArrow.setOnClickListener(v -> finish());

        // 하단 확인 버튼 기능
        binding.btnClose.setOnClickListener(v -> finish());

        receiptAdapter = new LedgerReceiptAdapter();
        binding.rvLedgerResult.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLedgerResult.setAdapter(receiptAdapter);

        if (startDate == null || endDate == null || startDate.isBlank() || endDate.isBlank()) {
            Toast.makeText(this, "조회 기간 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        fetchAndRenderPieChart(startDate, endDate);
    }

    private void fetchAndRenderPieChart(String startDate, String endDate) {
        String userId = CartManager.getInstance().getLoggedInUserId();
        if (userId == null || userId.isBlank()) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getInstance().getApiService()
                .getPurchaseHistoryByRange(
                        CartManager.getInstance().getAuthorizationHeader(),
                        userId,
                        startDate,
                        endDate
                )
                .enqueue(new Callback<ApiResponse<List<PurchaseHistoryItem>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<PurchaseHistoryItem>>> call,
                                           Response<ApiResponse<List<PurchaseHistoryItem>>> response) {
                        if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                            Toast.makeText(LedgerResultActivity.this, "조회에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<PurchaseHistoryItem> items = response.body().getData();
                        renderReceiptList(items == null ? new ArrayList<>() : items);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<PurchaseHistoryItem>>> call, Throwable t) {
                        Toast.makeText(LedgerResultActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderReceiptList(List<PurchaseHistoryItem> items) {
        if (items.isEmpty()) {
            binding.tvEmptyMessage.setText("해당 날짜의 구매 내역이 없습니다.");
            receiptAdapter.setData(new ArrayList<>());
            return;
        }

        Map<Long, List<PurchaseHistoryItem>> byOrder = new HashMap<>();
        for (PurchaseHistoryItem item : items) {
            Long orderId = item.getOrderId();
            if (orderId == null) {
                continue;
            }
            byOrder.computeIfAbsent(orderId, key -> new ArrayList<>()).add(item);
        }

        List<LedgerReceiptAdapter.LedgerReceipt> receipts = byOrder.entrySet().stream()
                .map(entry -> {
                    List<PurchaseHistoryItem> orderItems = entry.getValue();
                    PurchaseHistoryItem first = orderItems.get(0);
                    String lines = orderItems.stream()
                            .map(item -> String.format("%s x%d  %,d원",
                                    item.getPName(), item.getQuantity(), item.getLineTotal()))
                            .collect(Collectors.joining("\n"));
                    return new LedgerReceiptAdapter.LedgerReceipt(
                            entry.getKey(),
                            first.getOrderDate(),
                            lines,
                            first.getOrderTotalAmount()
                    );
                })
                .sorted(Comparator.comparingLong((LedgerReceiptAdapter.LedgerReceipt r) -> r.orderId).reversed())
                .collect(Collectors.toList());

        if (receipts.isEmpty()) {
            binding.tvEmptyMessage.setText("해당 날짜의 구매 내역이 없습니다.");
        } else {
            binding.tvEmptyMessage.setText("");
        }
        receiptAdapter.setData(receipts);
    }
}
