package com.example.customerapp.Customer.Household_Ledger;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityHouseholdLedgerBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.example.customerapp.Customer.Customer;
import com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket;
import com.example.customerapp.Customer.MyInfo;
import com.example.customerapp.DataModel.ApiResponse;
import com.example.customerapp.DataModel.CartManager;
import com.example.customerapp.DataModel.PurchaseHistoryItem;
import com.example.customerapp.DataModel.RetrofitClient;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class household_Ledger extends AppCompatActivity {
    private ActivityHouseholdLedgerBinding binding;
    // 시작 날짜와 종료 날짜를 저장할 변수
    private Calendar startDateCalendar;
    private Calendar endDateCalendar;
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHouseholdLedgerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // --- 1. 시작 날짜 설정 ---
        binding.buttonStartDate.setOnClickListener(v -> {
            DatePickerFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setOnDateSelectedListener((year, month, day) -> {
                String selectedDate = year + "년 " + month + "월 " + day + "일";
                binding.StartDate.setHint("선택한 날짜: " + selectedDate);

                // 시작 날짜 객체 저장
                startDateCalendar = Calendar.getInstance();
                startDateCalendar.set(year, month - 1, day);
            });
            dialogFragment.show(getSupportFragmentManager(), "datePicker");
        });

        // --- 2. 종료 날짜 설정 ---
        binding.buttonEndDate.setOnClickListener(v -> {
            DatePickerFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setOnDateSelectedListener((year, month, day) -> {
                endDateCalendar = Calendar.getInstance();
                endDateCalendar.set(year, month - 1, day);

                // 시작 날짜와 비교 검증
                if (startDateCalendar != null && endDateCalendar.before(startDateCalendar)) {
                    Toast.makeText(this, "종료 날짜는 시작 날짜 이후여야 합니다.", Toast.LENGTH_SHORT).show();
                    endDateCalendar = null; // 잘못된 선택 시 초기화
                } else {
                    String selectedDate = year + "년 " + month + "월 " + day + "일";
                    binding.EndDate.setHint("선택한 날짜: " + selectedDate);
                }
            });
            dialogFragment.show(getSupportFragmentManager(), "datePicker");
        });

        // --- 3. 초기화 기능 ---
        binding.buttonReset.setOnClickListener(v -> {
            binding.StartDate.setHint("연도-월-일 선택");
            binding.EndDate.setHint("연도-월-일 선택");
            startDateCalendar = null;
            endDateCalendar = null;
            Log.d("Reset", "날짜 초기화 완료");
        });

        // --- 4. 조회 기능 (최종 수정본: 결과 화면으로 이동) ---
        binding.buttonSearch.setOnClickListener(v -> {
            if (startDateCalendar == null || endDateCalendar == null) {
                Toast.makeText(this, "시작 날짜와 종료 날짜를 모두 설정해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String startDate = apiDateFormat.format(startDateCalendar.getTime());
            String endDate = apiDateFormat.format(endDateCalendar.getTime());

            // 결과 화면(LedgerResultActivity)으로 인텐트 전달 및 이동
            Intent intent = new Intent(household_Ledger.this, LedgerResultActivity.class);
            intent.putExtra("start_date", startDate);
            intent.putExtra("end_date", endDate);
            startActivity(intent);
        });

        // --- 5. 하단 네비게이션 바 설정 ---
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

        // 시스템 바Insets 설정 (하단 여백 0 유지)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        loadCurrentMonthPieChart();
    }

    private void loadCurrentMonthPieChart() {
        String userId = CartManager.getInstance().getLoggedInUserId();
        if (userId == null || userId.isBlank()) {
            binding.tvMonthEmptyMessage.setText("로그인 후 이번달 소비 비율을 볼 수 있습니다.");
            return;
        }

        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH) + 1;
        Calendar firstDay = (Calendar) now.clone();
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Calendar lastDay = (Calendar) now.clone();
        lastDay.set(Calendar.DAY_OF_MONTH, now.getActualMaximum(Calendar.DAY_OF_MONTH));

        String startDate = apiDateFormat.format(firstDay.getTime());
        String endDate = apiDateFormat.format(lastDay.getTime());

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
                            binding.tvMonthEmptyMessage.setText("이번달 구매 내역 조회에 실패했습니다.");
                            return;
                        }
                        List<PurchaseHistoryItem> items = response.body().getData();
                        renderMonthPieChart(items == null ? new ArrayList<>() : items, currentMonth);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<PurchaseHistoryItem>>> call, Throwable t) {
                        binding.tvMonthEmptyMessage.setText("네트워크 오류로 그래프를 불러오지 못했습니다.");
                    }
                });
    }

    private void renderMonthPieChart(List<PurchaseHistoryItem> items, int month) {
        Map<String, Integer> amountByCategory = new LinkedHashMap<>();
        for (PurchaseHistoryItem item : items) {
            String category = item.getCategory();
            if (category == null || category.isBlank()) {
                category = "기타";
            }
            int amount = item.getLineTotal();
            if (amount <= 0) {
                continue;
            }
            amountByCategory.put(category, amountByCategory.getOrDefault(category, 0) + amount);
        }

        if (amountByCategory.isEmpty()) {
            binding.pieChartMonth.clear();
            binding.tvMonthEmptyMessage.setText("이번달 구매 내역이 없습니다.");
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
        binding.pieChartMonth.animateY(850);

        Legend legend = binding.pieChartMonth.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);
        legend.setTextColor(Color.parseColor("#4B5563"));

        binding.pieChartMonth.invalidate();
    }
}
