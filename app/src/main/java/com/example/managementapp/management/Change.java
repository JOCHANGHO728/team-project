package com.example.managementapp.management;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivityChangeBinding;
public class Change extends AppCompatActivity {
    private ActivityChangeBinding binding;
    private String originalName;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvDisplayName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                loadProductByName(binding.tvDisplayName.getText().toString().trim());
            }
        });

        binding.tvDisplayName.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnter = event != null
                    && event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH || isEnter) {
                loadProductByName(binding.tvDisplayName.getText().toString().trim());
                return true;
            }
            return false;
        });

        binding.tvDisplayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pendingSearch != null) {
                    searchHandler.removeCallbacks(pendingSearch);
                }
                String input = s == null ? "" : s.toString().trim();
                if (input.length() < 2) {
                    return;
                }
                pendingSearch = () -> loadProductByName(input);
                searchHandler.postDelayed(pendingSearch, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 수량/가격 변경 기능
        binding.change.setOnClickListener(v -> {
            ProductDB products = new ProductDB(this);
            SQLiteDatabase db = products.getWritableDatabase();

            String name = binding.tvDisplayName.getText().toString().trim();
            String priceText = binding.editPrice.getText().toString().trim();
            String quantityText = binding.changeAmount.getText().toString().trim();
            String categoryText = binding.editCategory.getText().toString().trim();

            if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                Toast.makeText(this, "상품명/가격/수량을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                db.close();
                return;
            }
            if (originalName == null || originalName.isEmpty()) {
                originalName = findExactNameByInput(db, name);
                if (originalName == null) {
                    Toast.makeText(this, "먼저 상품명을 입력해 기존 상품을 불러와주세요.", Toast.LENGTH_SHORT).show();
                    db.close();
                    return;
                }
            }

            int price;
            int quantity;
            try {
                price = Integer.parseInt(priceText);
                quantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "가격/수량은 숫자로 입력하세요.", Toast.LENGTH_SHORT).show();
                db.close();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("price", price);
            values.put("quantity", quantity);
            values.put("category", categoryText);

            int result = db.update("products", values, "name = ? COLLATE NOCASE", new String[]{originalName});

            if (result > 0) {
                Toast.makeText(this, "상품 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                originalName = name;
                Log.d("DB_TEST", "UPDATE SUCCESS: " + name);
            } else {
                Toast.makeText(this, "수정 실패. 상품을 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                Log.d("DB_TEST", "UPDATE FAIL: " + name);
            }
            db.close();
        });

        //관리 화면으로 돌아가기
        binding.out.setOnClickListener(v -> {
            Intent intent = new Intent(Change.this, Management.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadProductByName(String name) {
        if (name.isEmpty()) {
            return;
        }

        ProductDB products = new ProductDB(this);
        SQLiteDatabase db = products.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT name, price, quantity, b_key, category FROM products " +
                        "WHERE name LIKE ? COLLATE NOCASE LIMIT 1",
                new String[]{"%" + name + "%"}
        );

        if (cursor.moveToFirst()) {
            String foundName = cursor.getString(0);
            int price = cursor.getInt(1);
            int quantity = cursor.getInt(2);
            String barcode = cursor.getString(3);
            String category = cursor.getString(4);

            originalName = foundName;
            binding.tvDisplayName.setText(foundName);
            binding.editPrice.setText(String.valueOf(price));
            binding.changeAmount.setText(String.valueOf(quantity));
            binding.tvDisplayBarcode.setText(barcode);
            binding.editCategory.setText(category);
            Toast.makeText(this, "상품 정보를 불러왔습니다.", Toast.LENGTH_SHORT).show();
        } else {
            originalName = null;
            binding.editPrice.setText("");
            binding.changeAmount.setText("");
            binding.tvDisplayBarcode.setText("");
            binding.editCategory.setText("");
            Toast.makeText(this, "해당 상품이 없습니다.", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    private String findExactNameByInput(SQLiteDatabase db, String inputName) {
        Cursor cursor = db.rawQuery(
                "SELECT name FROM products WHERE name LIKE ? COLLATE NOCASE LIMIT 1",
                new String[]{"%" + inputName + "%"}
        );
        String found = null;
        if (cursor.moveToFirst()) {
            found = cursor.getString(0);
        }
        cursor.close();
        return found;
    }
}
