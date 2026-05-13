package com.example.customerapp.Customer.Household_Ledger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customerapp.MainActivity;
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityHouseholdLedgerBinding;

import java.util.Calendar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.customerapp.Customer.Customer;
import com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket;
import com.example.customerapp.Customer.MyInfo;

public class household_Ledger extends AppCompatActivity {
    private ActivityHouseholdLedgerBinding binding;
    // 시작 날짜와 종료 날짜를 저장할 변수
    private Calendar startDateCalendar;
    private Calendar endDateCalendar;

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
            binding.StartDate.setHint("xx-xxxx-xx");
            binding.EndDate.setHint("xx-xxxx-xx");
            startDateCalendar = null;
            endDateCalendar = null;
            Log.d("Reset", "날짜 초기화 완료");
        });

        // --- 4. 조회 기능 (최종 수정본: 결과 화면으로 이동) ---
        binding.buttonSearch.setOnClickListener(v -> {
            String startHint = binding.StartDate.getHint().toString();
            String endHint = binding.EndDate.getHint().toString();

            // 날짜 선택 여부 확인
            if (startHint.contains("xx") || endHint.contains("xx")) {
                Toast.makeText(this, "조회 기간을 모두 설정해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 문구에서 실제 날짜 정보만 추출
            String start = startHint.replace("선택한 날짜: ", "");
            String end = endHint.replace("선택한 날짜: ", "");

            // 결과 화면(LedgerResultActivity)으로 인텐트 전달 및 이동
            Intent intent = new Intent(household_Ledger.this, LedgerResultActivity.class);
            intent.putExtra("start_date", start);
            intent.putExtra("end_date", end);
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
    }
}