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

public class household_Ledger extends AppCompatActivity {
    private ActivityHouseholdLedgerBinding binding;
    private TextView textPeriod;
    // 시작 날짜와 종료 날짜를 저장할 변수
    private Calendar startDateCalendar;
    private Calendar endDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHouseholdLedgerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*아이디어
        1. 사용자가 조회하고자 하는 기간을 정해서 입력
        2. 사용자가 입력해야 하는 정보를 모두 입력한 뒤에 버튼을 클릭
        3. 별도 텍스트 뷰로 기록된 데이터 열람 가능
        */

        // 시작 날짜 설정
        binding.buttonStartDate.setOnClickListener(v -> {
            DatePickerFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setOnDateSelectedListener((year, month, day) -> {
                String selectedDate = year + "년 " + month + "월 " + day + "일";
                binding.StartDate.setHint("선택한 날짜: " + selectedDate);

                // 시작 날짜 저장
                startDateCalendar = Calendar.getInstance();
                startDateCalendar.set(year, month - 1, day); // month는 0부터 시작
            });

            dialogFragment.show(getSupportFragmentManager(), "datePicker");
        });

        // 종료 날짜 설정
        binding.buttonEndDate.setOnClickListener(v -> {
            DatePickerFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setOnDateSelectedListener((year, month, day) -> {
                Calendar endDateCalendar = Calendar.getInstance();
                endDateCalendar.set(year, month - 1, day);

                // 시작 날짜와 비교
                if (startDateCalendar != null && endDateCalendar.before(startDateCalendar)) {
                    Toast.makeText(this, "종료 날짜는 시작 날짜 이후여야 합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    String selectedDate = year + "년 " + month + "월 " + day + "일";
                    binding.EndDate.setHint("선택한 날짜: " + selectedDate);
                }
            });

            dialogFragment.show(getSupportFragmentManager(), "datePicker");
        });

        // 초기화 기능
        binding.buttonReset.setOnClickListener(v -> {
            binding.StartDate.setHint("xx-xxxx-xx");
            binding.EndDate.setHint("xx-xxxx-xx");
            Log.d("Reset", "날짜 초기화 완료");
        });

        // 조회 기능
        binding.buttonSearch.setOnClickListener(v -> {
            String startDate = null;
            String endDate = null;
        });

        // 메인 페이지로 돌아가기
        binding.buttonOut.setOnClickListener(v -> {
            Intent intent = new Intent(household_Ledger.this, MainActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}