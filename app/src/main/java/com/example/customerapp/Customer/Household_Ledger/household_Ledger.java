package com.example.customerapp.Customer.Household_Ledger;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customerapp.Customer.Customer;
import com.example.customerapp.Customer.MyInfo;
import com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket;
import com.example.customerapp.DataModel.ApiResponse;
import com.example.customerapp.DataModel.CartManager;
import com.example.customerapp.DataModel.PurchaseHistoryItem;
import com.example.customerapp.DataModel.RetrofitClient;
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityHouseholdLedgerBinding;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class household_Ledger extends AppCompatActivity {
    private ActivityHouseholdLedgerBinding binding;
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private Calendar displayedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHouseholdLedgerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        displayedMonth = Calendar.getInstance();
        displayedMonth.set(Calendar.DAY_OF_MONTH, 1);

        binding.btnPrevMonth.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, -1);
            loadMonthPieChart();
        });

        binding.btnNextMonth.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, 1);
            loadMonthPieChart();
        });

        binding.btnDetailSearch.setOnClickListener(v ->
                startActivity(new Intent(this, LedgerDetailSearchActivity.class)));

        binding.bottomNavigation.setSelectedItemId(R.id.nav_ledger);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_ledger) {
                return true;
            } else if (id == R.id.nav_shopping) {
                intent = new Intent(this, Customer.class);
            } else if (id == R.id.nav_cart) {
                intent = new Intent(this, ShoppingBasket.class);
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        loadMonthPieChart();
    }

    private void loadMonthPieChart() {
        int year = displayedMonth.get(Calendar.YEAR);
        int month = displayedMonth.get(Calendar.MONTH) + 1;
        binding.tvMonthTitle.setText(month + "월");
        binding.tvMonthTotalAmount.setText(formatWon(0));
        binding.tvMonthEmptyMessage.setText("");

        String userId = CartManager.getInstance().getLoggedInUserId();
        if (userId == null || userId.isBlank()) {
            binding.pieChartMonth.clear();
            binding.tvMonthEmptyMessage.setText("로그인 후 월별 소비 비율을 볼 수 있습니다.");
            return;
        }

        Calendar firstDay = (Calendar) displayedMonth.clone();
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Calendar lastDay = (Calendar) displayedMonth.clone();
        lastDay.set(Calendar.DAY_OF_MONTH, displayedMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        String startDate = apiDateFormat.format(firstDay.getTime());
        String endDate = apiDateFormat.format(lastDay.getTime());

        RetrofitClient.getInstance().getApiService()
                .getPurchaseHistoryByRange(
                        userId,
                        startDate,
                        endDate
                )
                .enqueue(new Callback<ApiResponse<List<PurchaseHistoryItem>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<PurchaseHistoryItem>>> call,
                                           Response<ApiResponse<List<PurchaseHistoryItem>>> response) {
                        if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                            binding.pieChartMonth.clear();
                            binding.tvMonthEmptyMessage.setText("구매 내역 조회에 실패했습니다.");
                            binding.tvMonthTotalAmount.setText(formatWon(0));
                            return;
                        }
                        List<PurchaseHistoryItem> items = response.body().getData();
                        renderMonthPieChart(items == null ? new ArrayList<>() : items, year, month);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<PurchaseHistoryItem>>> call, Throwable t) {
                        binding.pieChartMonth.clear();
                        binding.tvMonthEmptyMessage.setText("네트워크 오류로 그래프를 불러오지 못했습니다.");
                        binding.tvMonthTotalAmount.setText(formatWon(0));
                    }
                });
    }

    private void renderMonthPieChart(List<PurchaseHistoryItem> items, int year, int month) {
        Map<String, Integer> amountByCategory = new LinkedHashMap<>();
        int totalAmount = 0;

        for (PurchaseHistoryItem item : items) {
            String category = item.getCategory();
            if (category == null || category.isBlank()) {
                category = "기타";
            }
            int amount = item.getLineTotal();
            if (amount <= 0) {
                continue;
            }
            totalAmount += amount;
            amountByCategory.put(category, amountByCategory.getOrDefault(category, 0) + amount);
        }

        binding.tvMonthTotalAmount.setText(formatWon(totalAmount));

        if (amountByCategory.isEmpty()) {
            binding.pieChartMonth.clear();
            binding.tvMonthEmptyMessage.setText(year + "년 " + month + "월 구매 내역이 없습니다.");
            binding.pieChartMonth.invalidate();
            return;
        }

        binding.tvMonthEmptyMessage.setText("");

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : amountByCategory.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                Color.parseColor("#A7C7E7"),
                Color.parseColor("#B5EAD7"),
                Color.parseColor("#FFDAC1"),
                Color.parseColor("#E2C2FF"),
                Color.parseColor("#CDE7BE"),
                Color.parseColor("#F9C6D3"),
                Color.parseColor("#C7CEEA"),
                Color.parseColor("#FDE2A7")
        );
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(4f);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(binding.pieChartMonth));

        binding.pieChartMonth.setUsePercentValues(true);
        binding.pieChartMonth.setData(pieData);
        binding.pieChartMonth.getDescription().setEnabled(false);
        binding.pieChartMonth.setCenterText(month + "월");
        binding.pieChartMonth.setCenterTextSize(18f);
        binding.pieChartMonth.setCenterTextColor(Color.parseColor("#222222"));
        binding.pieChartMonth.setDrawHoleEnabled(true);
        binding.pieChartMonth.setHoleColor(Color.WHITE);
        binding.pieChartMonth.setTransparentCircleAlpha(80);
        binding.pieChartMonth.setTransparentCircleRadius(56f);
        binding.pieChartMonth.setHoleRadius(50f);
        binding.pieChartMonth.setEntryLabelColor(Color.parseColor("#374151"));
        binding.pieChartMonth.setEntryLabelTextSize(11f);
        binding.pieChartMonth.setRotationAngle(300f);
        binding.pieChartMonth.animateY(450);

        Legend legend = binding.pieChartMonth.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);
        legend.setTextColor(Color.parseColor("#4B5563"));

        binding.pieChartMonth.invalidate();
    }

    private String formatWon(int amount) {
        return String.format(Locale.KOREA, "%,d원", amount);
    }
}
