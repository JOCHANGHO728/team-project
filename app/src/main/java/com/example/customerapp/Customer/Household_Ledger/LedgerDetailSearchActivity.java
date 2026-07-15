package com.example.customerapp.Customer.Household_Ledger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityLedgerDetailSearchBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LedgerDetailSearchActivity extends AppCompatActivity {
    private ActivityLedgerDetailSearchBinding binding;
    private Calendar startDateCalendar;
    private Calendar endDateCalendar;
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLedgerDetailSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBackArrow.setOnClickListener(v -> finish());

        binding.buttonStartDate.setOnClickListener(v -> {
            DatePickerFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setOnDateSelectedListener((year, month, day) -> {
                startDateCalendar = Calendar.getInstance();
                startDateCalendar.set(year, month - 1, day);
                binding.StartDate.setHint("선택한 날짜: " + formatKoreanDate(startDateCalendar));
            });
            dialogFragment.show(getSupportFragmentManager(), "datePicker");
        });

        binding.buttonEndDate.setOnClickListener(v -> {
            DatePickerFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setOnDateSelectedListener((year, month, day) -> {
                Calendar selectedEndDate = Calendar.getInstance();
                selectedEndDate.set(year, month - 1, day);

                if (startDateCalendar != null && selectedEndDate.before(startDateCalendar)) {
                    Toast.makeText(this, "종료 날짜는 시작 날짜 이후여야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                endDateCalendar = selectedEndDate;
                binding.EndDate.setHint("선택한 날짜: " + formatKoreanDate(endDateCalendar));
            });
            dialogFragment.show(getSupportFragmentManager(), "datePicker");
        });

        binding.buttonReset.setOnClickListener(v -> {
            binding.StartDate.setHint("연도-월-일 선택");
            binding.EndDate.setHint("연도-월-일 선택");
            startDateCalendar = null;
            endDateCalendar = null;
            Log.d("Reset", "날짜 초기화 완료");
        });

        binding.buttonSearch.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            if (startDateCalendar == null) {
                startDateCalendar = (Calendar) today.clone();
                binding.StartDate.setHint("선택한 날짜: " + formatKoreanDate(startDateCalendar));
            }
            if (endDateCalendar == null) {
                endDateCalendar = (Calendar) today.clone();
                binding.EndDate.setHint("선택한 날짜: " + formatKoreanDate(endDateCalendar));
            }

            if (endDateCalendar.before(startDateCalendar)) {
                Toast.makeText(this, "종료 날짜는 시작 날짜 이후여야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            String startDate = apiDateFormat.format(startDateCalendar.getTime());
            String endDate = apiDateFormat.format(endDateCalendar.getTime());

            Intent intent = new Intent(this, LedgerResultActivity.class);
            intent.putExtra("start_date", startDate);
            intent.putExtra("end_date", endDate);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String formatKoreanDate(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "년 "
                + (calendar.get(Calendar.MONTH) + 1) + "월 "
                + calendar.get(Calendar.DAY_OF_MONTH) + "일";
    }
}
