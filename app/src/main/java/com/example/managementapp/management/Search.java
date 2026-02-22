package com.example.managementapp.management;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivityManagementBinding;
import com.example.managementapp.databinding.ActivitySearchBinding;

public class Search extends AppCompatActivity {
    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 상품조회 기능
        binding.search.setOnClickListener(v -> {
            //내부 데이터베이스 객체 생성
            ProductDB Products = new ProductDB(this);
            SQLiteDatabase db = Products.getReadableDatabase();

            String id = binding.productID.getText().toString();
            Cursor cursor = db.rawQuery("SELECT * FROM products WHERE name = ?", new String[]{id});

            if (cursor != null && cursor.moveToFirst()) {
                // 데이터가 존재하는 경우
                int price = cursor.getInt(1);
                int quantity = cursor.getInt(2);
                String bKey = cursor.getString(3);
                String category = cursor.getString(4);

                binding.price.setText("  " + price);
                binding.quantity.setText("  " + quantity);
                binding.bKey.setText("  " + bKey);
                binding.category.setText("  " + category);

                Log.d("DB_TEST", id + " / " + price + " / " + quantity + " / " + bKey + " / " + category);
            } else {
                // 데이터가 없는 경우 → Toast 메시지 출력
                Toast.makeText(this, "해당 상품이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                Log.d("DB_TEST", "FAIL");
            }

            if (cursor != null) {
                cursor.close();
            }
            db.close();
        });

        // 관리 화면으로 돌아가기
        binding.out.setOnClickListener(v -> {
            Intent intent = new Intent(Search.this, Management.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}