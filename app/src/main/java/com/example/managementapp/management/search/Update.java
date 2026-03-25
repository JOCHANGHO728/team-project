package com.example.managementapp.management.search;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.managementapp.databinding.ActivityUpdateBinding;
import com.example.managementapp.management.ProductDB;
import com.example.managementapp.model.ProductResponse;
public class Update extends AppCompatActivity {
    private ActivityUpdateBinding binding;
    private ProductResponse product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. DetailSearch에서 보낸 데이터 받기
        product = (ProductResponse) getIntent().getSerializableExtra("product");

        if (product != null) {
            // 2. 기존 데이터를 입력창에 미리 채워넣기
            binding.editName.setText(product.getP_name());
            binding.editPrice.setText(String.valueOf(product.getP_price()));
            binding.editQuantity.setText(String.valueOf(product.getP_quantity()));
            binding.editCategory.setText(product.getCategory());
        }

        // 3. 수정 완료 버튼 클릭 시
        binding.btnSave.setOnClickListener(v -> {
            saveUpdatedData();
        });
    }

    // 상세 데이터 업데이트
    private void saveUpdatedData() {
        // 입력창에 사용된 문자열 읽기
        String newName = binding.editName.getText().toString().trim();
        String priceStr = binding.editPrice.getText().toString().trim();
        String quantityStr = binding.editQuantity.getText().toString().trim();
        String newCategory = binding.editCategory.getText().toString().trim();

        // 빈칸 검사
        if (newName.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // DB 객체 생성
        ProductDB productDB = new ProductDB(this);
        SQLiteDatabase db = productDB.getWritableDatabase();

        // 업데이트 값 설정
        ContentValues values = new ContentValues();
        values.put("name", newName);
        values.put("price", Integer.parseInt(priceStr));
        values.put("quantity", Integer.parseInt(quantityStr));
        values.put("category", newCategory);

        String targetBkey = product.getB_key();


        // 업데이트
        int result = db.update(
                "products", values, "b_key = ?", new String[]{targetBkey}
        );

        if (result > 0) {
            // 업데이트 성공
            Toast.makeText(this, "상품 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, DetailSearch.class);
            startActivity(intent);
            finish();
        } else {
            // 실패
            Toast.makeText(this, "수정 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
