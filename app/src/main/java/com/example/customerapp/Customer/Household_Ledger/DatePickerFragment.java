package com.example.customerapp.Customer.Household_Ledger;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnDateSelectedListener listener;

    // 날짜 선택 결과를 전달하기 위한 인터페이스 정의
    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int day);
    }

    // 외부(Activity 등)에서 리스너를 설정할 수 있도록 메서드 제공
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (listener != null) {
            // 선택된 날짜를 콜백으로 반환
            listener.onDateSelected(year, month + 1, day);
        }
    }
}